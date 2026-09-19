-- ConnectAI Sample Data Script
-- Instructions: Execute after creating user accounts via Supabase Auth

-- Sample query to check registered profiles
SELECT id, username, display_name, created_at FROM public.profiles;

-- Sample query to manually create a public test group if desired:
/*
INSERT INTO public.conversations (id, type, name, description, invite_code, is_private)
VALUES (
    '11111111-1111-1111-1111-111111111111',
    'GROUP',
    'General Lounge',
    'Welcome to ConnectAI public discussion lounge!',
    'CONNECT2026',
    false
);
*/
