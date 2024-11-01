INSERT INTO tag (name) VALUES
('Art'), ('Photography'), ('Design'),
('Travel'), ('Nature'), ('Fashion'),
('Tech'), ('Food'), ('Sports');

INSERT INTO patron (username, email) VALUES
('johndoe', 'johndoe@example.com'),
('janedoe', 'janedoe@example.com'),
('alexsmith', 'alexsmith@example.com');


INSERT INTO image (data) VALUES (null), (null), (null), (null), (null), (null), (null), (null), (null);

INSERT INTO post (title, description, image_id) VALUES
                                                               ('Sunset Photography', 'A beautiful sunset.', 1),
                                                               ('Modern Art', 'Abstract modern art piece.', 2),
                                                               ('Tech Trends', 'Latest technology trends in 2022.', 3),

                                                               ('Mountain Adventure', 'Climbing the high peaks.', 4),
                                                               ('Fashion Trends', '2022 Fashion looks and ideas.', 5),
                                                               ('Foodie Heaven', 'Best dishes to try this year.', 6),

                                                               ('Sports Review', '2022 Sports roundup.', 7),
                                                               ('Wildlife Photography', 'Capturing wildlife in nature.', 8),
                                                               ('Travel Guide', 'Top travel destinations for 2022.', 9);

INSERT INTO post_patron (post_id, patron_id) VALUES
                                                 (1, 1),
                                                 (2, 1),
                                                 (3, 1),
                                                 (4, 2),
                                                 (5, 2),
                                                 (6, 2),
                                                 (7, 3),
                                                 (8, 3),
                                                 (9, 3);

INSERT INTO post_tag (post_id, tag_id) VALUES (1, 2), (2, 1), (3, 7);
INSERT INTO post_tag (post_id, tag_id) VALUES (4, 4), (5, 6), (6, 8);
INSERT INTO post_tag (post_id, tag_id) VALUES (7, 9), (8, 3), (9, 4);

INSERT INTO user_tags (user_id, tag_id) VALUES (1, 1), (1, 2), (1, 3);
INSERT INTO user_tags (user_id, tag_id) VALUES (2, 4), (2, 5), (2, 6);
INSERT INTO user_tags (user_id, tag_id) VALUES (3, 7), (3, 8), (3, 9);


INSERT INTO folder (title, description) VALUES
('Johns Favorites', 'Favorite posts by John.'),
('Janes Collection', 'Collection of Janes preferred posts.'),
('Alexs Picks', 'Alexs top picks.');

INSERT INTO patron_folder (folder_id, patron_id) VALUES
                                                     (1, 1),
                                                     (2, 2),
                                                     (3, 3);

INSERT INTO folder_post (folder_id, post_id) VALUES
                                                 (1, 1), (1, 2), (1, 3);

INSERT INTO folder_post (folder_id, post_id) VALUES
                                                 (2, 4), (2, 5), (2, 6);

INSERT INTO folder_post (folder_id, post_id) VALUES
                                                 (3, 7), (3, 8), (3, 9);
