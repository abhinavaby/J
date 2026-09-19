-- ConnectAI Supabase Storage Policies
-- Storage bucket name: chat-attachments

-- Create private bucket 'chat-attachments' if not already created via dashboard
INSERT INTO storage.buckets (id, name, public)
VALUES ('chat-attachments', 'chat-attachments', false)
ON CONFLICT (id) DO NOTHING;

-- Policy 1: Authenticated users can upload attachments to chat-attachments bucket
CREATE POLICY "Authenticated users can upload attachments"
ON storage.objects FOR INSERT
TO authenticated
WITH CHECK (bucket_id = 'chat-attachments');

-- Policy 2: Users can download/read objects if they are authenticated
CREATE POLICY "Authenticated users can read attachments"
ON storage.objects FOR SELECT
TO authenticated
USING (bucket_id = 'chat-attachments');

-- Policy 3: Users can delete their own uploaded storage objects
CREATE POLICY "Users can delete own storage objects"
ON storage.objects FOR DELETE
TO authenticated
USING (bucket_id = 'chat-attachments' AND auth.uid() = owner);
