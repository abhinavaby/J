import { serve } from "https://deno.land/std@0.168.0/http/server.ts";
import { createClient } from "https://esm.sh/@supabase/supabase-js@2";

const OPENAI_API_KEY = Deno.env.get("OPENAI_API_KEY");
const SUPABASE_URL = Deno.env.get("SUPABASE_URL");
const SUPABASE_ANON_KEY = Deno.env.get("SUPABASE_ANON_KEY");

const corsHeaders = {
  "Access-Control-Allow-Origin": "*",
  "Access-Control-Allow-Headers": "authorization, x-client-info, apikey, content-type",
};

serve(async (req) => {
  if (req.method === "OPTIONS") {
    return new Response("ok", { headers: corsHeaders });
  }

  try {
    const authHeader = req.headers.get("Authorization");
    if (!authHeader) {
      return new Response(
        JSON.stringify({ error: "Missing Authorization header" }),
        { status: 401, headers: { ...corsHeaders, "Content-Type": "application/json" } }
      );
    }

    // Verify JWT token with Supabase Auth
    const supabase = createClient(SUPABASE_URL ?? "", SUPABASE_ANON_KEY ?? "", {
      global: { headers: { Authorization: authHeader } },
    });
    const { data: { user }, error: authError } = await supabase.auth.getUser();

    if (authError || !user) {
      return new Response(
        JSON.stringify({ error: "Unauthorized access: invalid or expired token" }),
        { status: 401, headers: { ...corsHeaders, "Content-Type": "application/json" } }
      );
    }

    const body = await req.json();
    const { action } = body;

    if (!OPENAI_API_KEY) {
      return new Response(
        JSON.stringify({ error: "OpenAI API Key is not configured on the server." }),
        { status: 500, headers: { ...corsHeaders, "Content-Type": "application/json" } }
      );
    }

    if (action === "summarize") {
      const { conversationId, messagesText } = body;

      if (!messagesText || typeof messagesText !== "string" || messagesText.trim().length === 0) {
        return new Response(
          JSON.stringify({ error: "No message text provided for summarization" }),
          { status: 400, headers: { ...corsHeaders, "Content-Type": "application/json" } }
        );
      }

      const prompt = `You are ConnectAI assistant. Summarize the following chat conversation history clearly into structured JSON format with key fields:
      - "summary": A concise overview of what was discussed.
      - "keyDecisions": Array of strings of agreed decisions.
      - "actionItems": Array of strings of tasks or action items.
      - "deadlines": Array of strings of any dates or deadlines mentioned.
      - "unansweredQuestions": Array of strings of unresolved questions.

      Return ONLY valid JSON matching this schema:
      {
        "summary": "...",
        "keyDecisions": ["..."],
        "actionItems": ["..."],
        "deadlines": ["..."],
        "unansweredQuestions": ["..."]
      }

      Chat messages:
      ${messagesText.substring(0, 4000)}`;

      const openAiRes = await fetch("https://api.openai.com/v1/chat/completions", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "Authorization": `Bearer ${OPENAI_API_KEY}`,
        },
        body: JSON.stringify({
          model: "gpt-4o-mini",
          response_format: { type: "json_object" },
          messages: [
            { role: "system", content: "You are a helpful chat summarizer assistant that outputs strict JSON." },
            { role: "user", content: prompt },
          ],
          temperature: 0.3,
          max_tokens: 800,
        }),
      });

      if (!openAiRes.ok) {
        const errText = await openAiRes.text();
        return new Response(
          JSON.stringify({ error: "Failed to query OpenAI for summary: " + errText }),
          { status: 502, headers: { ...corsHeaders, "Content-Type": "application/json" } }
        );
      }

      const openAiData = await openAiRes.json();
      const contentStr = openAiData.choices?.[0]?.message?.content || "{}";
      const structuredResult = JSON.parse(contentStr);

      return new Response(
        JSON.stringify(structuredResult),
        { status: 200, headers: { ...corsHeaders, "Content-Type": "application/json" } }
      );
    } else if (action === "rephrase") {
      const { text, tone } = body;

      if (!text || typeof text !== "string" || text.trim().length === 0) {
        return new Response(
          JSON.stringify({ error: "Text is required for rephrasing" }),
          { status: 400, headers: { ...corsHeaders, "Content-Type": "application/json" } }
        );
      }

      const requestedTone = tone || "Professional";
      const prompt = `Rephrase the following draft message to match a ${requestedTone} tone. Keep the original intent and core message intact. Return ONLY JSON: {"rephrasedText": "..."}

      Draft message: "${text.substring(0, 1000)}"`;

      const openAiRes = await fetch("https://api.openai.com/v1/chat/completions", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "Authorization": `Bearer ${OPENAI_API_KEY}`,
        },
        body: JSON.stringify({
          model: "gpt-4o-mini",
          response_format: { type: "json_object" },
          messages: [
            { role: "system", content: "You are an expert writing assistant that rephrases chat drafts into requested tones." },
            { role: "user", content: prompt },
          ],
          temperature: 0.4,
          max_tokens: 300,
        }),
      });

      if (!openAiRes.ok) {
        const errText = await openAiRes.text();
        return new Response(
          JSON.stringify({ error: "Failed to query OpenAI for rephrasing: " + errText }),
          { status: 502, headers: { ...corsHeaders, "Content-Type": "application/json" } }
        );
      }

      const openAiData = await openAiRes.json();
      const contentStr = openAiData.choices?.[0]?.message?.content || "{}";
      const structuredResult = JSON.parse(contentStr);

      return new Response(
        JSON.stringify(structuredResult),
        { status: 200, headers: { ...corsHeaders, "Content-Type": "application/json" } }
      );
    } else {
      return new Response(
        JSON.stringify({ error: `Unknown action: ${action}` }),
        { status: 400, headers: { ...corsHeaders, "Content-Type": "application/json" } }
      );
    }
  } catch (err: any) {
    return new Response(
      JSON.stringify({ error: err.message || "Internal server error" }),
      { status: 500, headers: { ...corsHeaders, "Content-Type": "application/json" } }
    );
  }
});
