INSERT INTO patron (email, username, password) VALUES
('john.doe@example.com', 'johndoe', '$2a$12$fgb9AENL5BlDba6PRgOLD.bec85ofD16BTZpGouDu0FOK1LIiRAG6'),
('emma.watson@example.com', 'emmawatson', '$2a$12$h2I0UOU4XTUcgh635IPIw./1z63ngNb.sdJwQPxgj3cqXM8osbdxq'),
('michael.smith@example.com', 'michaelsmith', '$2a$12$E4mI2xLcU15urGbkKFmK..Y6tXb2tG9ATNay.e63BxJJArVqTOYuG');

INSERT INTO folder (id, title, description) VALUES
(1, 'Travel Adventures', 'A collection of travel memories and stories.'),
(2, 'Photography Portfolio', 'My best shots and photography projects.'),
(3, 'Emma''s Journal', 'Personal reflections and thoughts from Emma.'),
(4, 'Project Ideas', 'Potential projects and creative ideas.'),
(5, 'Michael''s Musings', 'Michael''s insights and random thoughts.'),
(6, 'Favorite Recipes', 'A list of my favorite recipes and cooking tips.');

INSERT INTO image (id, file) VALUES
(1, 'https://res.cloudinary.com/drlpmttmc/image/upload/v1731334018/baba_yaga_z5bcci.jpg'),
(2, 'https://res.cloudinary.com/drlpmttmc/image/upload/v1731334472/beautiful_landscape_with_sheep_tttbex.jpg'),
(3, 'https://res.cloudinary.com/drlpmttmc/image/upload/v1731334493/beautifull_car_br2cwz.jpg'),
(4, 'https://res.cloudinary.com/drlpmttmc/image/upload/v1731334514/berserk_art_gyoylt.png'),
(5, 'https://res.cloudinary.com/drlpmttmc/image/upload/v1731334543/berserk_metka_zhertvy_gmzt78.jpg'),
(6, 'https://res.cloudinary.com/drlpmttmc/image/upload/v1731334567/burlaki_uhu4xh.jpg'),
(7, 'https://res.cloudinary.com/drlpmttmc/image/upload/v1731334619/call_of_ktulhu_mj5vvs.jpg'),
(8, 'https://res.cloudinary.com/drlpmttmc/image/upload/v1731335371/grumpy_cat_ozwife.jpg'),
(9, 'https://res.cloudinary.com/drlpmttmc/image/upload/v1731334672/italic_hlzbyw.jpg'),
(10, 'https://res.cloudinary.com/drlpmttmc/image/upload/v1731334694/lone_light_f5sr4n.jpg'),
(11, 'https://res.cloudinary.com/drlpmttmc/image/upload/v1731334763/makima_tjecro.jpg'),
(12, 'https://res.cloudinary.com/drlpmttmc/image/upload/v1731334786/nyancat_sjfqz9.jpg'),
(13, 'https://res.cloudinary.com/drlpmttmc/image/upload/v1731334857/pes_kurazh_hcg9hw.jpg'),
(14, 'https://res.cloudinary.com/drlpmttmc/image/upload/v1731334882/samurai_cat_jislot.png'),
(15, 'https://res.cloudinary.com/drlpmttmc/image/upload/v1731334913/samurai_lanscape_endt7g.jpg'),
(16, 'https://res.cloudinary.com/drlpmttmc/image/upload/v1731334940/seryi_kot_guaace.jpg'),
(17, 'https://res.cloudinary.com/drlpmttmc/image/upload/v1731334962/skelet_lzmvvc.jpg'),
(18, 'https://res.cloudinary.com/drlpmttmc/image/upload/v1731334989/smokin_girl_hjuztt.jpg'),
(19, 'https://res.cloudinary.com/drlpmttmc/image/upload/v1731335046/snow_barsik_vj8p2e.jpg'),
(20, 'https://res.cloudinary.com/drlpmttmc/image/upload/v1731335065/stranded_rocket_kqfuuc.png'),
(21, 'https://res.cloudinary.com/drlpmttmc/image/upload/v1731335100/the_invincible_game_ccuccj.jpg'),
(22, 'https://res.cloudinary.com/drlpmttmc/image/upload/v1731335200/three_bears_in_koloda_xxlsc9.jpg'),
(23, 'https://res.cloudinary.com/drlpmttmc/image/upload/v1731335223/winter_girl_campfire_qv5kp6.jpg'),
(24, 'https://res.cloudinary.com/drlpmttmc/image/upload/v1731335255/winter_train_v8ueba.jpg');


INSERT INTO post (id, image_id, title, description) VALUES
-- Posts for John Doe
(1, 1, 'Witch''s Swamp Hut', 'An eerie, mist-covered swamp with an old wooden hut elevated on stilts.'),
(2, 2, 'Peaceful Countryside Farm', 'A charming rustic farmhouse set in a lush, green landscape, surrounded by gentle hills and grazing sheep.'),
(3, 3, 'High-Speed Racer', 'A sleek, futuristic race car with vibrant orange and blue accents, designed for high-speed performance.'),
(4, 4, 'Guts Facing Darkness', 'A powerful monochromatic illustration from the Berserk manga, depicting Guts standing resolute against an ominous, otherworldly void.'),
(5, 5, 'Berserk Brand of Sacrifice', 'The haunting Brand of Sacrifice from the Berserk series, a mark that signifies a person’s fate as a sacrifice.'),
(6, 6, 'Wanderers in the Mist', 'A group of mysterious, hooded figures slowly makes their way through a foggy, desolate landscape.'),
(7, 7, 'Call of Cthulhu', 'A lone figure faces the colossal form of Cthulhu, emerging from the ocean depths with glowing red eyes and massive tentacles.'),
(8, 8, 'Grumpy Cat', 'An illustration of the iconic Grumpy Cat, with a trademark unimpressed expression.'),

-- Posts for Emma Watson
(9, 9, 'The Abyss', 'A solitary figure clings to the edge of a rocky precipice, gazing down into a vast, mysterious void.'),
(10, 10, 'Lone Streetlight', 'A single streetlight illuminates an empty, snow-covered street in the dead of night. The quiet, dark atmosphere conveys a sense of isolation and calm amidst the cold.'),
(11, 11, 'Makima', 'The ominous atmosphere hints at hidden power and control.'),
(12, 12, 'Nyan Cat in Space', 'An adorable, iconic Nyan Cat with a pop-tart body soaring through space, leaving a trail of rainbow colors behind.'),
(13, 13, 'Courage''s Haunted House', 'The lonely house of Courage the Cowardly Dog, standing isolated under a vast, starry sky with a windmill creaking in the background.'),
(14, 14, 'Samurai Cat Warrior', 'A fierce-looking cat with intricate tattoos and a katana in its mouth, exuding the spirit of a samurai.'),
(15, 15, 'Wanderer at Dusk', 'A lone samurai stands on a mountain ridge at dusk, silhouetted against a red-tinted sky.'),
(16, 16, 'Majestic British Shorthair', 'A calm and curious British Shorthair cat, resting on the floor with its iconic round face and plush gray fur.'),

-- Posts for Michael Smith
(17, 17, 'The Last Stand', 'A lone skeleton stands tall on a pile of skulls, silhouetted against a deep blue sky. The surreal and haunting scene evokes a sense of desolation and eternal solitude'),
(18, 18, 'Contemplation at Dusk', 'A silhouette of a girl sitting by the window, smoking as the sun sets over the city skyline.'),
(19, 19, 'Curious Snow Leopard Cub', 'An adorable young snow leopard exploring the forest, its fur blending seamlessly with the autumn leaves.'),
(20, 20, 'Stranded Rocket', 'A lone astronaut stands next to a massive, futuristic rocket on a desolate desert-like planet.'),
(21, 21, 'The Invincible Explorer', 'An astronaut''s helmet submerged in desert sands, with a giant planet looming in the red sky above.'),
(22, 22, 'Curious Trio', 'Three adorable bears peeking into a hollow log, each with a unique expression of curiosity and wonder.'),
(23, 23, 'Winter''s Warmth', 'A girl sits by a cozy campfire under the mesmerizing northern lights, savoring a moment of solitude in the snowy wilderness.'),
(24, 24, 'Snowy Train Station', 'A lone train waits at a quiet, snow-covered station surrounded by winter trees. Footprints in the snow tell a silent story of fleeting journeys.');


INSERT INTO post_patron (patron_id, post_id) VALUES
-- Posts by John Doe
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 6), (1, 7), (1, 8), 
-- Posts by Emma Watson
(1, 9), (1, 10), (2, 11), (2, 12), (2, 13), (2, 14), (2, 15), (2, 16), 
-- Posts by Michael Smith
(2, 17), (2, 18), (2, 19), (2, 20), (3, 21), (3, 22), (3, 23), (3, 24);

INSERT INTO folder_post (folder_id, post_id) VALUES
-- Folder associations for John Doe
(1, 1), (1, 2), (1, 3), (2, 4), (2, 5),
-- Folder associations for Emma Watson
(3, 11), (3, 12), (3, 13), (4, 14), (4, 15),
-- Folder associations for Michael Smith
(5, 21), (5, 22), (5, 23), (6, 24);

INSERT INTO patron_folder (folder_id, patron_id) VALUES
(1, 1), (2, 1), (3, 2), (4, 2), (5, 3), (6, 3);

INSERT INTO tag (id, name) VALUES
(1, 'Travel'), (2, 'Nature'),
(3, 'Lifestyle'), (4, 'Photography'),
(5, 'Adventure'), (6, 'Reflection');

INSERT INTO post_tag (post_id, tag_id) VALUES
-- Tags for John Doe's posts
(1, 1), (2, 1), (3, 1), (4, 2), (5, 2),
-- Tags for Emma Watson's posts
(11, 3), (12, 3), (13, 4), (14, 4), (15, 4),
-- Tags for Michael Smith's posts
(21, 5), (22, 5), (23, 5), (24, 6);

INSERT INTO user_tags (tag_id, user_id) VALUES
(1, 1), (2, 1), (3, 2), (4, 2), (5, 3), (6, 3);

