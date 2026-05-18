USE wolfgang_db;

INSERT INTO instruments (id, name, wave_type) VALUES
(1, 'Sinusoïdale',       'sine'),
(2, 'Carrée',            'square'),
(3, 'En dents de scie',  'sawtooth'),
(4, 'Triangulaire',      'triangle');

# tous les users ont 'password' comme mdp
INSERT INTO users (id, username, email, password, is_admin, is_verified, created_at, updated_at) VALUES
(1, 'asgore', 'asgore@underground.mt',       '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 1, 1, '2026-01-10 09:00:00', '2026-01-10 09:00:00'),
(2, 'toriel', 'toriel@ruins.underground',    '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 0, 1, '2026-01-15 11:00:00', '2026-01-15 11:00:00'),
(3, 'sans',   'sans@snowdin.underground',    '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 0, 1, '2026-02-01 14:30:00', '2026-02-01 14:30:00'),
(4, 'undyne', 'undyne@waterfall.underground','$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 0, 1, '2026-02-20 08:15:00', '2026-02-20 08:15:00');

INSERT INTO compositions (id, title, description, tempo, access_type, owner_id, created_at, updated_at) VALUES
(1, 'Megalovania', 'tuturutum tututum', 200, 'public',  3, '2026-02-01 10:00:00', '2026-03-10 12:00:00'),
(2, 'Heartache', 'rip toriel', 85, 'private', 2, '2026-02-14 16:00:00', '2026-02-14 16:00:00'),
(3, 'Bonetrousle', 'NYEH HEH HEH', 145, 'public',  3, '2026-03-05 20:00:00', '2026-04-01 09:30:00'),
(4, 'Death by Glamour', '', 160, 'link', 4, '2026-03-20 22:00:00', '2026-03-20 22:00:00'),
(5, 'Bergentruckung', 'ASGORE LE GOAT', 55, 'public',  1, '2026-04-10 13:00:00', '2026-04-12 18:00:00'),
(6, 'Hopes and Dreams', 'Asriel </3', 120, 'private', 1, '2026-05-02 11:00:00', '2026-05-02 11:00:00');


INSERT INTO informations (title, description, created_at) VALUES
('Bienvenue sur Wolfgang', 'La plateforme de composition collaborative est maintenant en ligne. Créez, partagez et collaborez sur vos compositions musicales.', '2026-01-10 10:00:00'),
('Nouvelle fonctionnalité : partage par lien', 'Il est désormais possible de partager une composition via un lien unique, sans rendre la composition publique.', '2026-03-01 14:00:00'),
('Mise à jour de l''éditeur', 'L''éditeur de partition a été amélioré : meilleure gestion des tempos et nouveaux instruments disponibles.', '2026-04-15 09:30:00');

INSERT INTO composition_members (composition_id, user_id, role) VALUES
-- owners
(1, 3, 'owner'),
(2, 2, 'owner'),
(3, 3, 'owner'),
(4, 4, 'owner'),
(5, 1, 'owner'),
(6, 1, 'owner'),
-- partages croisés
(1, 2, 'editor'),   -- toriel peut éditer Megalovania
(1, 4, 'viewer'),   -- undyne voit Megalovania
(3, 1, 'viewer'),   -- asgore voit Bonetrousle
(5, 2, 'editor'),   -- toriel peut éditer Bergentrückung
(6, 4, 'editor');   -- undyne peut éditer Hopes and Dreams
