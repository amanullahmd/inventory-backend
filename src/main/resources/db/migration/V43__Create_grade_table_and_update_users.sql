-- Create grades table
CREATE TABLE grades (
    grade_id BIGSERIAL PRIMARY KEY,
    grade_number INTEGER NOT NULL UNIQUE,
    description VARCHAR(255)
);

-- Populate grades 1-20
INSERT INTO grades (grade_number, description) VALUES
(1, 'Grade 1 - Highest'),
(2, 'Grade 2'),
(3, 'Grade 3'),
(4, 'Grade 4'),
(5, 'Grade 5'),
(6, 'Grade 6'),
(7, 'Grade 7'),
(8, 'Grade 8'),
(9, 'Grade 9'),
(10, 'Grade 10'),
(11, 'Grade 11'),
(12, 'Grade 12'),
(13, 'Grade 13'),
(14, 'Grade 14'),
(15, 'Grade 15'),
(16, 'Grade 16'),
(17, 'Grade 17'),
(18, 'Grade 18'),
(19, 'Grade 19'),
(20, 'Grade 20 - Lowest');

-- Add grade_id to users table
ALTER TABLE users ADD COLUMN grade_id BIGINT;
ALTER TABLE users ADD CONSTRAINT fk_users_grade FOREIGN KEY (grade_id) REFERENCES grades(grade_id);
