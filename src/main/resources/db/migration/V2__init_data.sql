INSERT INTO public.users (id, email, name, password, image_name, image_type, image_url) VALUES
('208fc822-70ee-497d-8a94-71bae7b8ffce', 'jdoe@gmail.com', 'John Doe', '$2a$12$Tzbv/bY9aJ5lXHnSe5YDPOUuSeeN7HTR9qrmgGnxaYUSmO5lcZUCW', 'default.png', 'image/png', '/api/v1/users/photo/default.png');


INSERT INTO public.topic (id, description, name, topic_url) VALUES (1, '', 'Business Intelligence', '/api/v1/topics/Business-Intelligence');
INSERT INTO public.topic (id, description, name, topic_url) VALUES (2, '', 'Business Strategy', '/api/v1/topics/Business-Strategy');
INSERT INTO public.topic (id, description, name, topic_url) VALUES (3, '', 'Python', '/api/v1/topics/Python');
INSERT INTO public.topic (id, description, name, topic_url) VALUES (4, '', 'Data Science', '/api/v1/topics/Data-Science');
INSERT INTO public.topic (id, description, name, topic_url) VALUES (5, '', 'Data Engineering', '/api/v1/topics/Data-Engineering');
INSERT INTO public.topic (id, description, name, topic_url) VALUES (6, '', 'Music', '/api/v1/topics/Music');


-- Only for test purpose --
INSERT INTO public.password_resets(id, expiry, token, used, user_id) VALUES
('3e468594-6b0b-424a-9dc9-4c561f1005cd', '2023-06-12T11:15:31Z', '3d1fb1a7-91bb-46c9-842f-5bfe2b53d32f', true, '208fc822-70ee-497d-8a94-71bae7b8ffce'),
('88600cae-b553-42ec-b50d-df5c9db311f6', CURRENT_DATE + TIME '23:59:59', 'ba24e2aa-2987-496c-9240-2f3b8fb48f91', false, '208fc822-70ee-497d-8a94-71bae7b8ffce' );
-- END --