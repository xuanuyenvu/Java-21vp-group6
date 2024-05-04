-- Database: bsms

-- DROP DATABASE IF EXISTS bsms;

-- Instruction
-- If there is existing database, please delete and create a new one
-- Run this sql file.

-- The results of the select count(*) for each table:
-- 180 book
-- 31 author
-- 24 publisher
-- 23 category
-- 272 book category
-- 4 importsheet
-- 50 importedbook
-- 101 member
-- 12 account, 3 of which admin
-- 4 ordersheet
-- 12 orderedbook

-- Tables

create table if not exists Account (
    id serial primary key,
    phone char(10) unique check (phone ~ '^[0-9]{10}$') not null,
    password varchar(255) check (length(password) > 4) not null,
    name varchar(255) check (length(name) > 0),
    gender varchar(10) check (gender in ('Male', 'Female', 'Other')),
    email varchar(255) unique check (email ~ '^[A-Za-z0-9._%-]+@[A-Za-z0-9.-]+$' or email is null),
    address varchar(255) check (length(address) > 0 or address is null),
    isAdmin boolean default false not null,
    isLocked boolean default false not null
);

create table if not exists Publisher (
    id serial primary key,
    name varchar(255) unique check (length(name) > 0) not null,
    email varchar(255) unique check (email ~ '^[A-Za-z0-9._%-]+@[A-Za-z0-9.-]+$' or email is null),
    address varchar(255) check (length(address) > 0 or address is null),
    isHidden boolean default false not null
);

create table if not exists Author (
    id serial primary key,
    name varchar(255) unique check (length(name) > 0) not null,
    overview text,
    isHidden boolean default false not null
);

create table if not exists Book (
    id serial primary key,
    title varchar(255) unique check (length(title) > 0) not null,
    authorId int references Author(id) not null,
    publisherId int references Publisher(id) not null,
    pageCount int check (pageCount >= 0) not null,
    publishDate date check (publishDate < current_date) not null,
    dimension varchar(30) check (dimension ~ '^[0-9]+(\.[0-9]+)?x[0-9]+(\.[0-9]+)?x[0-9]+(\.[0-9]+)? cm$'),
    translatorName varchar(255),
    overview text,
    quantity int check (quantity >= 0) not null,
    salePrice decimal(12, 2) check (salePrice >= 0 OR salePrice IS NULL),
    isHidden boolean default false not null,
    hiddenParentCount int check (hiddenParentCount >= 0) not null,
    maxImportPrice decimal(12, 2)
);

create table if not exists Category (
    id serial primary key,
    name varchar(255) unique check (length(name) > 0) not null,
    isHidden boolean default false not null
);

create table if not exists BookCategory (
    bookId int references Book(id) not null,
    categoryId int references Category(id) not null,
    primary key (bookId, categoryId)
);

create table if not exists ImportSheet (
    id serial primary key,
    employeeInChargeId int references Account(id) not null,
    importDate date default current_date not null,
    totalCost decimal(16, 2) check (totalCost >= 0) not null
);

create table if not exists ImportedBook (
    importSheetId int references ImportSheet(id) not null,
    bookId int references Book(id) not null,
    quantity int check (quantity >= 0) not null,
    pricePerBook decimal(12, 2) check (pricePerBook >= 0) not null,
    primary key (importSheetId, bookId)
);

create table if not exists Member (
    id serial primary key,
    phone char(10) unique check (phone ~ '^[0-9]{10}$') not null,
    name varchar(255) check (length(name) > 0) not null,
    gender varchar(10) check (gender in ('Male', 'Female', 'Other')) not null,
    dateOfBirth date check (dateOfBirth < current_date) not null,
    email varchar(255) unique check (email ~ '^[A-Za-z0-9._%-]+@[A-Za-z0-9.-]+$'),
    address varchar(255) check (length(address) > 0)
);

create table if not exists OrderSheet (
    id serial primary key,
    memberId int references Member(id) not null,
    employeeInChargeId int references Account(id) not null,
    orderDate date default current_date not null,
    discountedTotalCost decimal(14, 2) check (discountedTotalCost >= 0) not null
);

create table if not exists OrderedBook (
    orderSheetId int references OrderSheet(id) not null,
    bookId int references Book(id) not null,
    quantity int check (quantity >= 0) not null,
    pricePerBook decimal(12, 2) check (pricePerBook >= 0) not null,
    primary key (orderSheetId, bookId)
);



CREATE OR REPLACE FUNCTION update_saleprice()
RETURNS TRIGGER AS $$
DECLARE
    max_orderdate TIMESTAMP;
    orderdate TIMESTAMP;
BEGIN
    SELECT MAX(os.orderdate)
    INTO max_orderdate
    FROM orderedbook ob
    JOIN ordersheet os ON os.id = ob.ordersheetid
    WHERE ob.bookid = NEW.bookid;

    SELECT os.orderdate
    INTO orderdate
    FROM ordersheet os
    WHERE os.id = NEW.ordersheetid;
	
    IF max_orderdate IS NOT NULL AND orderdate > max_orderdate THEN
        UPDATE book
        SET saleprice = NEW.priceperbook
        WHERE id = NEW.bookid;
    ELSEIF max_orderdate IS NULL THEN
        UPDATE book
        SET saleprice = NEW.priceperbook
        WHERE id = NEW.bookid;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER update_saleprice_trigger
BEFORE INSERT ON OrderedBook
FOR EACH ROW
EXECUTE FUNCTION update_saleprice();


CREATE OR REPLACE FUNCTION update_saleprice_postimport()
RETURNS TRIGGER AS $$
BEGIN	
    UPDATE BOOK
    SET SALEPRICE = NEW.maxImportPrice * 1.1
    WHERE BOOK.ID = NEW.ID;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER update_saleprice_postimport_trigger
AFTER UPDATE OF maxImportPrice ON BOOK
FOR EACH ROW
EXECUTE FUNCTION update_saleprice_postimport();


CREATE OR REPLACE FUNCTION update_quantity_postorder()
RETURNS TRIGGER AS $$
DECLARE
    max_orderdate TIMESTAMP;
    orderdate TIMESTAMP;
BEGIN
    UPDATE BOOK
	SET QUANTITY = QUANTITY - NEW.QUANTITY
	WHERE BOOK.ID = NEW.BOOKID;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER update_quantity_postorder_trigger
BEFORE INSERT ON OrderedBook
FOR EACH ROW
EXECUTE FUNCTION update_quantity_postorder();


INSERT INTO Publisher (id, name, email, address, isHidden) VALUES (1, 'Bloomsbury Publishing', 'contact@bloomsbury.com', '50 Bedford Square, London, WC1B 3DP', 'false');
INSERT INTO Publisher (id, name, email, address, isHidden) VALUES (2, 'BoD – Books on Demand', NULL, '22848 Norderstedt', 'false');
INSERT INTO Publisher (id, name, email, address, isHidden) VALUES (3, 'Good Press', 'goodpress.sundays@gmail.com', '32 St Andrews Street, Glasgow, G1 5PD, UK', 'false');
INSERT INTO Publisher (id, name, email, address, isHidden) VALUES (4, 'Hachette UK', 'enquiries@hachette.co.uk', '50 Victoria Embankment, London, EC4Y 0DZ, UK', 'false');
INSERT INTO Publisher (id, name, email, address, isHidden) VALUES (5, 'HarperCollins', 'consumercare@harpercollins.com', 'HarperCollins Publishers, 195 Broadway, New York, NY 10007, US', 'false');
INSERT INTO Publisher (id, name, email, address, isHidden) VALUES (6, 'HarperCollins UK', 'enquiries@harpercollins.co.uk', 'HarperCollins Publishers Limited, 103 Westerhill Road, Bishopbriggs, Glasgow, G64 2QT', 'false');
INSERT INTO Publisher (id, name, email, address, isHidden) VALUES (7, 'HMH Books For Young Readers', 'orders@hmhco.com', NULL, 'false');
INSERT INTO Publisher (id, name, email, address, isHidden) VALUES (8, 'Independently Published', 'jimb@bookpublishing.com', NULL, 'false');
INSERT INTO Publisher (id, name, email, address, isHidden) VALUES (9, 'J-Novel Club', 'contact@j-novel.club', NULL, 'false');
INSERT INTO Publisher (id, name, email, address, isHidden) VALUES (10, 'J. Sterling', 'jsterling@jennster.com', NULL, 'false');
INSERT INTO Publisher (id, name, email, address, isHidden) VALUES (11, 'Kodansha America LLC', 'hello@kodanshacomics.com', NULL, 'false');
INSERT INTO Publisher (id, name, email, address, isHidden) VALUES (12, 'Library of Alexandria', 'depository@bibalex.org', 'Bibliotheca Alexandrina, El Shatby, P.O. Box 138, Alexandria 21526', 'false');
INSERT INTO Publisher (id, name, email, address, isHidden) VALUES (13, 'Modernista', 'info@modernista.se', 'Kvarngatan 10, »Garaget« 118 47 Stockholm', 'false');
INSERT INTO Publisher (id, name, email, address, isHidden) VALUES (14, 'National Geographic Books', 'history@natgeo.com', 'National Geographic, Editorial Services - 1145 17th Street NW, Washington, DC 20036', 'false');
INSERT INTO Publisher (id, name, email, address, isHidden) VALUES (15, 'Oxford University Press', 'EXPORTtradequeries@oup.com', NULL, 'false');
INSERT INTO Publisher (id, name, email, address, isHidden) VALUES (16, 'Penguin Random House', 'consumerservices@penguinrandomhouse.com', '1745 Broadway, New York, NY 10019, USA', 'false');
INSERT INTO Publisher (id, name, email, address, isHidden) VALUES (17, 'Read Books Ltd', NULL, '14 Clews Road, Redditch, England, B98 7ST', 'false');
INSERT INTO Publisher (id, name, email, address, isHidden) VALUES (18, 'Scholastic Inc.', 'slpservice@scholastic.com', 'Scholastic Library Publishing, P.O. Box 3765 Jefferson City, MO 65102-3765', 'false');
INSERT INTO Publisher (id, name, email, address, isHidden) VALUES (19, 'Seven Seas Entertainment', NULL, '3463 STATE ST STE 545,SANTA BARBARA,California,US', 'false');
INSERT INTO Publisher (id, name, email, address, isHidden) VALUES (20, 'Simon and Schuster', 'enquiries@simonandschuster.co.uk', '1st Floor, 222 Gray''s Inn Road, London WC1X8HB, United Kingdom', 'false');
INSERT INTO Publisher (id, name, email, address, isHidden) VALUES (21, 'Tor Books', 'foreignrights@stmartins.com', '120 Broadway, New York, NY 10271', 'false');
INSERT INTO Publisher (id, name, email, address, isHidden) VALUES (22, 'VIZ Media LLC', NULL, '1355 Market St, Ste 200, San Francisco, CA 94103', 'false');
INSERT INTO Publisher (id, name, email, address, isHidden) VALUES (23, 'Wildside Press LLC', NULL, '9710 Traville Gateway Dr, #234, Rockville MD 20850', 'false');
INSERT INTO Publisher (id, name, email, address, isHidden) VALUES (24, 'Yen Press LLC', 'customerservice@yenpress.com', '150 W 30th Street, 19th floor, New York, New York 10001', 'false');
INSERT INTO Author (id, name, overview, ishidden) VALUES (1, 'Agatha Christie', 'Agatha Christie was an English writer known for her detective novels featuring iconic characters like Hercule Poirot and Miss Marple. She is one of the best-selling authors of all time, with works such as Murder on the Orient Express and The Murder of Roger Ackroyd becoming classics of the genre.', 'false');
INSERT INTO Author (id, name, overview, ishidden) VALUES (2, 'Ali Hazelwood', 'Ali Hazelwood is a contemporary romance author known for her debut novel The Love Hypothesis, which gained popularity for its witty dialogue, engaging characters, and incorporation of STEM themes into the romance genre.', 'false');
INSERT INTO Author (id, name, overview, ishidden) VALUES (3, 'Arthur Conan Doyle', 'Sir Arthur Conan Doyle was a Scottish writer and physician, best known for creating the legendary detective Sherlock Holmes. His works, including A Study in Scarlet and The Hound of the Baskervilles, have had a profound influence on mystery literature and popular culture.', 'false');
INSERT INTO Author (id, name, overview, ishidden) VALUES (4, 'Charles Dickens', 'Charles Dickens was an English novelist of the Victorian era, renowned for his vivid characters and portrayal of social injustices. His notable works include A Tale of Two Cities, Great Expectations, and Oliver Twist, which continue to be studied and adapted for various media.', 'false');
INSERT INTO Author (id, name, overview, ishidden) VALUES (5, 'Disney Books', 'Disney Books encompasses a wide range of literature inspired by Disney films, characters, and franchises. These books cater to audiences of all ages and include adaptations, original stories, and educational materials based on Disney''s extensive catalog of animated and live-action productions.', 'false');
INSERT INTO Author (id, name, overview, ishidden) VALUES (6, 'Eiichiro Oda', 'Eiichiro Oda is a Japanese manga artist best known for creating the long-running and highly successful manga series One Piece. His imaginative world-building, dynamic characters, and epic storytelling have made One Piece one of the most popular manga series worldwide.', 'false');
INSERT INTO Author (id, name, overview, ishidden) VALUES (7, 'F. Scott Fitzgerald', 'F. Scott Fitzgerald was an American novelist and short story writer associated with the Jazz Age and the Lost Generation. His masterpiece, The Great Gatsby, is considered a classic of American literature, exploring themes of wealth, love, and the American Dream.', 'false');
INSERT INTO Author (id, name, overview, ishidden) VALUES (8, 'George R. R. Martin', 'George R. R. Martin is an American novelist and screenwriter, best known for his epic fantasy series A Song of Ice and Fire, which was adapted into the acclaimed television series Game of Thrones. His intricate plots, morally ambiguous characters, and richly detailed world-building have captivated readers worldwide.', 'false');
INSERT INTO Author (id, name, overview, ishidden) VALUES (9, 'Harper Lee', 'Harper Lee was an American novelist best known for her Pulitzer Prize-winning novel To Kill a Mockingbird. This seminal work explores themes of racial injustice and moral growth in the American South and remains a classic of modern American literature.', 'false');
INSERT INTO Author (id, name, overview, ishidden) VALUES (10, 'Houghton Mifflin Harcourt', 'Houghton Mifflin Harcourt is a global publishing company known for producing educational materials, textbooks, fiction, and non-fiction books for readers of all ages. It is one of the largest providers of educational resources in the United States.', 'false');
INSERT INTO Author (id, name, overview, ishidden) VALUES (11, 'Isaac Asimov', 'Isaac Asimov was an American writer and professor of biochemistry, best known for his science fiction works such as the Foundation series and the Robot series. Asimov''s influence extends beyond literature, as he made significant contributions to science communication and popularized scientific concepts.', 'false');
INSERT INTO Author (id, name, overview, ishidden) VALUES (12, 'J. D. Salinger', 'J. D. Salinger was an American novelist best known for his novel The Catcher in the Rye, which remains a classic of adolescent literature. Salinger''s works often explore themes of alienation, identity, and the search for authenticity.', 'false');
INSERT INTO Author (id, name, overview, ishidden) VALUES (13, 'J. K. Rowling', 'J. K. Rowling is a British author best known for creating the Harry Potter series, which has become one of the best-selling book series in history. Rowling''s magical world-building and engaging storytelling have captivated readers of all ages.', 'false');
INSERT INTO Author (id, name, overview, ishidden) VALUES (14, 'J. R. R. Tolkien', 'J. R. R. Tolkien was an English writer, philologist, and professor, best known for his high fantasy works The Hobbit and The Lord of the Rings. His richly detailed mythologies, languages, and epic narratives have had a profound influence on the fantasy genre.', 'false');
INSERT INTO Author (id, name, overview, ishidden) VALUES (15, 'J. Sterling', 'J. Sterling is an author known for contemporary romance novels such as The Perfect Game series and 10 Years Later. Her works often explore themes of love, friendship, and personal growth.', 'false');
INSERT INTO Author (id, name, overview, ishidden) VALUES (16, 'Jane Austen', 'Jane Austen was an English novelist known for her romantic fiction set among the British landed gentry. Her novels, including Pride and Prejudice, Sense and Sensibility, and Emma, are celebrated for their wit, social commentary, and exploration of relationships.', 'false');
INSERT INTO Author (id, name, overview, ishidden) VALUES (17, 'John Steinbeck', 'John Steinbeck was an American author and Nobel Prize laureate known for his works depicting the struggles of the working class and marginalized groups during the Great Depression. His notable works include The Grapes of Wrath, Of Mice and Men, and East of Eden.', 'false');
INSERT INTO Author (id, name, overview, ishidden) VALUES (18, 'Kanehito Yamada', 'Kanehito Yamada is a Japanese author known for Frieren: Beyond Journey''s End, a manga series that has gained significant popularity and recognition. The series, which began serialization in Weekly Shōnen Sunday in April 2020, has been adapted into an anime television series and has won several awards, including the Manga Taishō and the Shogakukan Manga Award. Yamada''s work is celebrated for its engaging storytelling and unique characters, contributing to the series'' success and its place among the best-selling manga series of all time', 'false');
INSERT INTO Author (id, name, overview, ishidden) VALUES (19, 'Kiyohiko Azuma', 'Kiyohiko Azuma, born on May 27, 1968, is a renowned Japanese manga artist and author. He gained fame for his yonkoma comedy manga series Azumanga Daioh, which was later adapted into an anime. Azuma also created Yotsuba&!, a slice-of-life manga series about a five-year-old girl, serialized in Dengeki Daioh. His distinctive art style has made a significant impact in the manga industry', 'false');
INSERT INTO Author (id, name, overview, ishidden) VALUES (20, 'Mark Twain', 'Mark Twain was an American writer, humorist, and lecturer, best known for his novels The Adventures of Tom Sawyer and Adventures of Huckleberry Finn. Twain''s works often satirize societal norms and explore themes of freedom, morality, and the American experience.', 'false');
INSERT INTO Author (id, name, overview, ishidden) VALUES (21, 'Misaki Takamatsu', 'Misaki Takamatsu is a Japanese manga artist known for creating the manga series Skip and Loafer, which has been well-received by readers, with several editions published. Her works include Ameko Hime, Canaria-tachi no Fune, Okaeri Aureole, and Skip to Loafer, showcasing her versatility in storytelling and artistry ', 'false');
INSERT INTO Author (id, name, overview, ishidden) VALUES (22, 'Natsu Hyuuga', 'Natsu Hyūga is a Japanese novelist and light novel author, known for her work on The Apothecary Diaries. She resides in Fukuoka Prefecture. Hyūga has contributed to various works, including web novels, light novels, and manga, showcasing her versatility in the literary world. Her works often explore themes of mystery, romance, and historical settings, with The Apothecary Diaries being a notable example of her storytelling abilities', 'false');
INSERT INTO Author (id, name, overview, ishidden) VALUES (23, 'Ocean Vuong', 'Ocean Vuong is a Vietnamese-American poet and novelist known for his critically acclaimed debut novel On Earth We''re Briefly Gorgeous. Vuong''s work explores themes of identity, family, and the immigrant experience through lyrical prose.', 'false');
INSERT INTO Author (id, name, overview, ishidden) VALUES (24, 'Ryoko Kui', 'Ryoko Kui, known by her Japanese name 九井諒子 (Kui Ryōko), is a renowned Japanese manga artist best recognized for her series Delicious in Dungeon. She began her career by uploading a fantasy comic named UORIR from 2008 to 2012 on a personal website. Kui made her debut with a collection of one-shots titled The Dragon''s School Is on Top of the Mountain in March 2011, followed by Terrarium in Drawer in 2013. Since 2014, she has been publishing Delicious in Dungeon in the monthly magazine Harta. Her work has garnered significant attention and accolades, including the Excellence Award in the Manga Division at the 17th Annual Japan Media Arts Festival for Terrarium in Drawer and multiple Manga Taishō nominations for her works, including Delicious in Dungeon in 2019 ', 'false');
INSERT INTO Author (id, name, overview, ishidden) VALUES (25, 'Scholastic', 'Scholastic is a global publishing and education company known for its wide range of books, educational materials, and literacy programs aimed at children, young adults, and educators. It is one of the largest publishers of children''s books in the world.', 'false');
INSERT INTO Author (id, name, overview, ishidden) VALUES (26, 'Stephen King', 'Stephen King is an American author of horror, supernatural fiction, suspense, and fantasy novels. With works such as The Shining, It, and The Stand, King has become one of the most prolific and recognizable authors in contemporary literature', 'false');
INSERT INTO Author (id, name, overview, ishidden) VALUES (27, 'Tatsuki Fujimoto', 'Tatsuki Fujimoto is a renowned Japanese manga artist, born on October 10, 1992, or 1993, in Nikaho, Akita Prefecture, Japan. He began drawing at an early age and, due to the lack of preparatory schools near his home, attended painting classes where his grandparents practiced oil painting. Fujimoto graduated from Tohoku University of Art and Design in Yamagata, Yamagata Prefecture, in 2014 with a degree in Western painting. His career in manga began with one-shot works, leading to his first major serialized work, Fire Punch, published on Shueisha''s Shōnen Jump+ online magazine from 2016 to 2018. His second major work, Chainsaw Man, was published in Weekly Shōnen Jump from 2018 to 2020 and has since become one of the most popular manga series, winning numerous awards, including the Shogakukan Manga Award and the Harvey Awards. Fujimoto''s unique background, diving straight into creating his own original one-shots without working as an assistant, has contributed to his success in the manga industry [0][1][3].', 'false');
INSERT INTO Author (id, name, overview, ishidden) VALUES (28, 'Tsubasa Yamaguchi', 'Tsubasa Yamaguchi is a renowned Japanese manga artist born on June 26 in Tokyo. After graduating from Tokyo University of the Arts, she began her career by publishing two one-shots in Good! Afternoon. In 2016, she launched her first full series, a manga adaptation of Makoto Shinkai''s She and Her Cat, in Monthly Afternoon. Following its completion, she launched Blue Period, which has been highly acclaimed, winning the 13th Manga Taishō and the Kodansha Manga Award in the general category in 2020. An anime television series adaptation of Blue Period aired in October 2021. Yamaguchi''s work is celebrated for its unique aesthetics and texture, reflecting her sensitivity and technique in capturing the beauty of shading in a monochrome world [0][1][2][4].', 'false');
INSERT INTO Author (id, name, overview, ishidden) VALUES (29, 'William Shakespeare', 'William Shakespeare, often referred to as the Bard, was an English poet, playwright, and actor, widely regarded as the greatest writer in the English language and the world''s pre-eminent dramatist. Born in Stratford-upon-Avon, England, in 1564, Shakespeare''s career as a playwright and poet spanned the late Renaissance and early modern periods. His plays and poems have been translated into every major living language and are performed more often than those of any other playwright. Shakespeare''s works include the histories, tragedies, comedies, and sonnets, which are collectively known as the Shakespearean canon. His plays have been performed more often than those of any other playwright, and his works continue to be studied and performed around the globe.', 'false');
INSERT INTO Author (id, name, overview, ishidden) VALUES (30, 'Yukinobu Tatsu', 'Yukinobu Tatsu is a Japanese manga artist known for his work on the Dandadan manga series. He began his career as an assistant to Tatsuki Fujimoto on Fire Punch and Chainsaw Man, and also worked as an assistant to Kaku Yuuji on Jigokuraku. Tatsu''s journey to success was marked by challenges, as his early submissions to Shonen Jump were not serialized, and much of his early work is no longer available online. Despite these setbacks, Tatsu persevered and eventually created Dandadan, which became his breakout hit. The series, which started serialization in April 2021, is celebrated for its unique art style and storytelling, showcasing Tatsu''s talent and creativity. Tatsu''s work on Dandadan has garnered international attention and anticipation for its anime adaptation [0][4].', 'false');
INSERT INTO Author (id, name, overview, ishidden) VALUES (31, 'Yuu Toyota', 'Yuu Toyota, born on February 10, is a Japanese manga creator known for her work in various genres, including shojo and slice-of-life. She has a significant following and is recognized for her contributions to the manga industry. Toyota''s work spans across different series, showcasing her versatility and talent in storytelling. Her manga, such as 30-sai made Dotei Da to Mahotsukai ni Nareru rashii, have garnered attention and ratings, indicating her popularity and the quality of her work. Toyota is also active on social media platforms like Twitter and Pixiv, where she engages with her fans and shares her creative process [0][2][3].', 'false');
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (1, 'Marple: Twelve New Stories (2022)', 1, 5, 338, '22.88x16.55x1.55 cm', NULL, 'A brand new collection of short stories featuring the Queen of Crime’s legendary detective Jane Marple, penned by twelve remarkable bestselling and acclaimed authors.', 0, 0, '2022-09-15', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (2, 'Midsummer Mysteries - Tales from the Queen of Mystery (2023)', 1, 5, 269, '24.35x15.98x2.30 cm', NULL, 'Agatha Christie’s most famous characters—including Hercule Poirot and Miss Marple—solve even the most devilish of conundrums as the sun beats down in this all-new summer themed collection from the Queen of Mystery. Summertime—as the temperature rises, so does the potential for evil. From Cornwall to the French Riviera, whether against a background of Delphic temples or English country houses, Agatha Christie’s most famous characters solve complicated puzzles as the stakes heat up. Pull up a deckchair and enjoy plot twists and red herrings galore from the bestselling fiction writer of all time. Includes the stories: The Blood-Stained Pavement The Double Clue A Death on the Nile Harlequin’s Lane The Adventure of the Italian Nobleman Jane in Search of a Job The Disappearance of Mr. Davenheim The Idol House of Astarte The Rajah’s Emerald The Oracle at Delphi The Adventure of the Sinister Stranger The Incredible Theft', 0, 0, '2023-05-09', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (3, 'The Clue of the Chocolate Box (2019)', 1, 23, 19, '24.93x15.19x1.53 cm', NULL, 'Hastings mentions his belief that Poirot had never known failure in his professional career. Poirot said that was not true and relates the one occasion when he failed to solve a crime, years earlier when he was a police detective in Brussels.', 0, 0, '2019-02-04', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (4, 'The Murder of Roger Ackroyd & The Hollow Bundle (2022)', 1, 5, 628, '23.92x13.43x1.63 cm', NULL, 'TWO BESTSELLING MYSTERIES IN ONE GREAT PACKAGE! In THE MURDER OF ROGER ACROYD, Roger Ackroyd knew too much. He knew that the woman he loved had poisoned her brutal first husband. He suspected also that someone had been blackmailing her. Then, tragically, came the news that she had taken her own life with an apparent drug overdose. However, the evening post brought Roger one last fatal scrap of information, but before he could finish reading the letter, he was stabbed to death. Luckily one of Roger’s friends and the newest resident to retire to this normally quiet village takes over—none other than Monsieur Hercule Poirot . . . Not only beloved by generations of readers, The Murder of Roger Ackroyd was one of Agatha Christie’s own favorite works—a brilliant whodunit that firmly established the author’s reputation as the Queen of Mystery. In THE HOLLOW, a far-from-warm welcome greets Hercule Poirot as he arrives for lunch at Lucy Angkatell’s country house. A man lies dying by the swimming pool, his blood dripping into the water. His wife stands over him, holding a revolver. As Poirot investigates, he begins to realize that beneath the respectable surface lies a tangle of family secrets and everyone becomes a suspect.', 0, 0, '2022-01-04', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (5, 'The Mysterious Affair at Styles Agatha Christie - Original Ending (2021)', 1, 8, 182, '23.59x15.38x2.34 cm', NULL, 'The Mysterious Affair at Styles Agatha Christie''s first novel, The Mysterious Affair at Styles, was the result of a dare from her sister Madge who challenged her to write a story. The story begins when Hastings is sent back to England from the First World War due to injury and is invited to spend his sick leave at the beautiful Styles Court by his old friend John Cavendish. Here, Hastings meets John''s step-mother, Mrs Inglethorp, and her new husband, Alfred. Despite the tranquil surroundings Hastings begins to realise that all is not right. When Mrs Inglethorp is found poisoned, suspicion falls on the family, and another old friend, Hercule Poirot, is invited to investigate.', 0, 0, '2021-08-18', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (6, 'Love on the Brain (2022)', 2, 16, 369, '22.49x14.45x1.77 cm', NULL, 'An Instant New York Times Bestseller A #1 LibraryReads and Indie Next Pick! From the New York Times bestselling author of The Love Hypothesis comes a new STEMinist rom-com in which a scientist is forced to work on a project with her nemesis—with explosive results. Like an avenging, purple-haired Jedi bringing balance to the mansplained universe, Bee Königswasser lives by a simple code: What would Marie Curie do? If NASA offered her the lead on a neuroengineering project—a literal dream come true after years scraping by on the crumbs of academia—Marie would accept without hesitation. Duh. But the mother of modern physics never had to co-lead with Levi Ward. Sure, Levi is attractive in a tall, dark, and piercing-eyes kind of way. And sure, he caught her in his powerfully corded arms like a romance novel hero when she accidentally damseled in distress on her first day in the lab. But Levi made his feelings toward Bee very clear in grad school—archenemies work best employed in their own galaxies far, far away. Now, her equipment is missing, the staff is ignoring her, and Bee finds her floundering career in somewhat of a pickle. Perhaps it’s her occipital cortex playing tricks on her, but Bee could swear she can see Levi softening into an ally, backing her plays, seconding her ideas…devouring her with those eyes. And the possibilities have all her neurons firing. But when it comes time to actually make a move and put her heart on the line, there’s only one question that matters: What will Bee Königswasser do?', 0, 0, '2022-08-23', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (7, 'Love, Theoretically (2023)', 2, 16, 401, '22.61x16.61x1.97 cm', NULL, 'An Indie Next and Library Reads Pick! Rival physicists collide in a vortex of academic feuds and fake dating shenanigans in this delightfully STEMinist romcom from the New York Times bestselling author of The Love Hypothesis and Love on the Brain. The many lives of theoretical physicist Elsie Hannaway have finally caught up with her. By day, she’s an adjunct professor, toiling away at grading labs and teaching thermodynamics in the hopes of landing tenure. By other day, Elsie makes up for her non-existent paycheck by offering her services as a fake girlfriend, tapping into her expertly honed people-pleasing skills to embody whichever version of herself the client needs. Honestly, it’s a pretty sweet gig—until her carefully constructed Elsie-verse comes crashing down. Because Jack Smith, the annoyingly attractive and arrogant older brother of her favorite client, turns out to be the cold-hearted experimental physicist who ruined her mentor’s career and undermined the reputation of theorists everywhere. And he’s the same Jack Smith who rules over the physics department at MIT, standing right between Elsie and her dream job. Elsie is prepared for an all-out war of scholarly sabotage but…those long, penetrating looks? Not having to be anything other than her true self when she’s with him? Will falling into an experimentalist’s orbit finally tempt her to put her most guarded theories on love into practice?', 0, 0, '2023-06-13', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (8, 'The Love Hypothesis (2021)', 2, 16, 401, '22.51x15.93x1.45 cm', NULL, 'The Instant New York Times Bestseller and TikTok Sensation! As seen on THE VIEW! A BuzzFeed Best Summer Read of 2021 When a fake relationship between scientists meets the irresistible force of attraction, it throws one woman''s carefully calculated theories on love into chaos. As a third-year Ph.D. candidate, Olive Smith doesn''t believe in lasting romantic relationships--but her best friend does, and that''s what got her into this situation. Convincing Anh that Olive is dating and well on her way to a happily ever after was always going to take more than hand-wavy Jedi mind tricks: Scientists require proof. So, like any self-respecting biologist, Olive panics and kisses the first man she sees. That man is none other than Adam Carlsen, a young hotshot professor--and well-known ass. Which is why Olive is positively floored when Stanford''s reigning lab tyrant agrees to keep her charade a secret and be her fake boyfriend. But when a big science conference goes haywire, putting Olive''s career on the Bunsen burner, Adam surprises her again with his unyielding support and even more unyielding...six-pack abs. Suddenly their little experiment feels dangerously close to combustion. And Olive discovers that the only thing more complicated than a hypothesis on love is putting her own heart under the microscope.', 0, 0, '2021-09-14', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (9, 'Murder - Vintage Minis (2020)', 3, 16, 160, '22.24x14.52x1.74 cm', NULL, '‘Well, Watson, we seem to have fallen upon evil days’ Sherlock Holmes: the quintessential British hero and the world''s most popular detective. Through his powers of deduction, and with the help of his faithful companion Dr Watson, Holmes takes on all manner of devious criminals and dangerous villains – and wins. But the cases involving murder are the most dastardly of them all... Selected from The Complete Sherlock Holmes VINTAGE MINIS: GREAT MINDS. BIG IDEAS. LITTLE BOOKS. A series of short books by the world’s greatest writers on the experiences that make us human Also in the Vintage Minis series: Power by William Shakespeare Independence by Charlotte Bronte London by Charles Dickens', 0, 0, '2020-03-05', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (10, 'London - Vintage Minis (2020)', 4, 16, 88, '24.19x15.99x1.27 cm', NULL, '‘Wealth and beggary, virtue and vice, repletion and the direst hunger, all treading on each other and crowding together’ Could any writer portray London better than Charles Dickens? Dickens knew the city inside out, walking the streets day and night, in all weathers, and drawing inspiration from everything he saw. The fog, the mud, the churning river, the clamour of church bells, and at every corner schemes of business or pleasure – this is Dickens’s London in the company of some of his most memorable characters. Selected from the work of Charles Dickens VINTAGE MINIS: GREAT MINDS. BIG IDEAS. LITTLE BOOKS. A series of short books by the world’s greatest writers on the experiences that make us human Also in the Vintage Minis series: Murder by Arthur Conan Doyle Power by William Shakespeare Independence by Charlotte Bronte', 0, 0, '2020-03-05', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (11, 'Oliver Twist - The Parish Boy''s Progress - With Appreciations and Criticisms By G. K. Chesterton (2015)', 4, 17, 330, '24.66x13.00x1.14 cm', NULL, 'Charles Dickens''s second novel, “Oliver Twist” was first published as a serial from 1837 to 1839 and centres around the story of orphan Oliver Twist, who was born in a workhouse and sold as an apprentice to an undertaker. When he manages to escape, Oliver travels to London where he encounters the “Artful Dodger” and his gang of pickpockets. A gritty representation of the London underworld, “Oliver” famously exposed the hardships of the poor, especially the terrible treatment of orphans in the mid-19th century. Charles John Huffam Dickens (1812–1870) was an English writer and social critic famous for having created some of the world''s most well-known fictional characters. His works became unprecedentedly popular during his life, and today he is commonly regarded as the greatest Victorian-era novelist. Although perhaps better known for such works as “Great Expectations” or “A Christmas Carol”, Dickens first gained success with the 1836 serial publication of “The Pickwick Papers”, which turned him almost overnight into an international literary celebrity thanks to his humour, satire, and astute observations concerning society and character. This classic work is being republished now in a new edition complete with an introductory chapter from “Appreciations and Criticisms of the Works of Charles Dickens” by G. K. Chesterton.', 0, 0, '2015-05-11', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (12, 'Mickey Mouse Funhouse: Adventures in Dino-Sitting (2023)', 5, 16, 0, '22.28x13.33x2.43 cm', NULL, 'When Mickey and the gang dino-sit for a Triceratops named T-Top and five dino eggs, the friends must keep their young dino-charges safe from harm, especially when the eggs start to hatch. Follow Mickey and his friends on this dino adventure that''s sure to be fun for the whole gang! You may also want to considering adding these other books to your Disney Collection: World of Reading: Disney Junior Mickey: Friendship Tales Disney Junior Minnie: One Unicorny Day World of Reading: Minnie''s Bow-Toons: Daisy''s Crazy Hair Day Alice''s Wonderland Bakery: Unforgettable Unbirthday World of Reading: Meet the Firebuds', 0, 0, '2023-12-19', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (13, 'Mickey Mouse Funhouse: Worlds of Fun! - My First Comic Reader! (2024)', 5, 16, 0, '22.47x15.29x1.94 cm', NULL, NULL, 0, 0, '2024-03-16', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (14, 'My First Disney Classics: A Christmas Carol (2023)', 5, 16, 0, '22.53x16.18x2.47 cm', NULL, 'Introduce your little one to this timeless classic about the true spirit of Christmas alongside Mickey and his friends! This retelling of Charles Dickens''s beloved Christmas classic is full of the baby-friendly faces of Mickey and his pals and baby-friendly themes, like kindess and sharing. Prompts on the page will encourage toddlers to engage with the story as they count the gold coins on Scrooge''s desk or name the colors on the Christmas tree. With simple text, bright illustrations, and sturdy pages, this 24-page board book is perfect for cultivating your little one''s love of literature. Consider adding these other Disney books to your collection: My First Disney Classics Bedtime Storybook My First Disney Classics The Legend of Sleepy Hollow Ariel Loves the Ocean First Word Book Twinkle, Twinkle, Little Star Say Please, Stitch!', 0, 0, '2023-09-05', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (15, 'One Piece Box Set 4: Dressrosa to Reverie - Volumes 71-90 with Premium (2021)', 6, 22, 4328, '24.78x14.85x1.64 cm', NULL, '"Join the adventures—and misadventures—of Monkey D. Luffy and his swashbuckling crew in their search for the “One Piece,” the greatest treasure in the world. As a child, Monkey D. Luffy dreamed of becoming King of the Pirates. But his life changed when he accidentally gained the power to stretch like rubber…at the cost of never being able to swim again! Years, later, Luffy sets off in search of the ""One Piece,"" said to be the greatest treasure in the world... The fourth One Piece Box Set contains volumes 71–90, which make up the Dressrosa, Zou, Whole Cake Island and Reverie arcs. Along with these thrilling, action-packed stories, the box set also includes an exclusive premium booklet and double-sided color poster."', 0, 0, '2021-09-07', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (16, 'One Piece Color Walk Compendium: East Blue to Skypiea (2018)', 6, 22, 0, '23.06x16.13x1.41 cm', NULL, 'Gorgeous color art from Eiichiro Oda’s One Piece! The first three Color Walk art books collected into one beautiful compendium. Color images and special illustrations from the world’s most popular manga, One Piece! This compendium features over 300 pages of beautiful color art as well as interviews between the creator and other famous manga artists, including Taiyo Matsumoto, the creator of Tekkonkinkreet. This first volume covers the early parts of the series—from the East Blue arc where the main characters of the Straw Hat pirates first meet, to the Skypiea arc where Luffy and friends face their greatest adventures yet!', 0, 0, '2018-07-03', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (17, 'One Piece Color Walk Compendium: New World to Wano (2022)', 6, 22, 336, '22.59x14.31x1.34 cm', NULL, 'Gorgeous color art from Eiichiro Oda’s One Piece! Volumes 7, 8, and 9 of the Color Walk art books are collected into one beautiful compendium. Color images and special illustrations from the world’s most popular manga, One Piece! This compendium features over 300 pages of beautiful color art as well as interviews between the creator and other famous manga artists. Keep up with the colorful adventures of the One Piece gang! This next installment continues following the Straw Hats through their Paramount War adventures into the arc of the New World in vivid, vibrant detail, with special interviews and author commentary you don’t want to miss!', 0, 0, '2022-10-25', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (18, 'One Piece Pirate Recipes (2021)', 6, 22, 98, '24.83x14.64x1.84 cm', NULL, 'You can’t become King of the Pirates on an empty stomach! Monkey D. Luffy has defeated dozens of rivals, and that kind of success takes a whole lot of energy! Fortunately, the pirate cook Sanji stands by Luffy’s side, ready to support his captain with flaming kicks and piping-hot meals! Hearty and filling, Sanji’s recipes keep the greatest pirate crew in the world well-fed, and his flashy techniques will take your culinary skills to the next level! -- VIZ Media', 0, 0, '2021-11-23', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (19, 'One Piece, Vol. 95 - Oden''s Adventure (2020)', 6, 22, 202, '22.48x14.28x2.24 cm', NULL, 'Kaido is already the strongest creature in the world—and he’s about to form an alliance that would make his Animal Kingdom pirates even more powerful!! Meanwhile, Luffy and the Straw Hats continue to recruit members to their side. But unbeknownst to them, the balance of power in the world is beginning to change... -- VIZ Media', 0, 0, '2020-12-01', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (20, 'One Piece, Vol. 96 - I Am Oden, And I Was Born To Boil (2021)', 6, 22, 193, '22.67x15.66x1.25 cm', NULL, 'During his journey with Whitebeard’s crew, Oden encounters the legendary future pirate king Gold Roger! What does their meeting mean for the world? And what has Orochi been up to while Oden was gone from Wano?! -- VIZ Media', 0, 0, '2021-04-06', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (21, 'One Piece, Vol. 97 - My Bible (2021)', 6, 22, 193, '24.53x15.48x2.21 cm', NULL, 'With the raid on Onigashima in sight, Kanjuro’s betrayal is revealed. He’s kidnapped Momonosuke! While the samurai are in shock, Luffy, Law and Kid work together to bring a ray of hope. It’s time to take on Kaido and save Wano!! -- VIZ Media', 0, 0, '2021-08-24', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (22, 'One Piece, Vol. 98 - Vassals Of Glory (2021)', 6, 22, 195, '22.81x13.36x1.52 cm', NULL, 'As the battle of Onigashima heats up, Kaido’s daughter Yamato actually wants to join Luffy’s side. Meanwhile, Kaido reveals his grand plans and, together with Big Mom, prepares to plunge the entire world into fear! -- VIZ Media', 0, 0, '2021-12-07', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (23, 'One Piece, Vol. 99 - Straw Hat Luffy (2022)', 6, 22, 195, '24.31x14.12x1.73 cm', NULL, 'As Luffy heads to the top of Onigashima for a direct confrontation with Kaido, the rest of the Straw Hats fight their own battles. The numbers aren’t on their side, but perhaps some surprise allies will help even the odds! -- VIZ Media', 0, 0, '2022-05-03', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (24, 'One Piece, Vol. 99 (2022)', 6, 22, 200, '22.26x14.30x1.45 cm', NULL, 'Join Monkey D. Luffy and his swashbuckling crew in their search for the ultimate treasure, One Piece! As a child, Monkey D. Luffy dreamed of becoming King of the Pirates. But his life changed when he accidentally gained the power to stretch like rubber...at the cost of never being able to swim again! Years, later, Luffy sets off in search of the One Piece, said to be the greatest treasure in the world... As Luffy heads to the top of Onigashima for a direct confrontation with Kaido, the rest of the Straw Hats fight their own battles. The numbers aren’t on their side, but perhaps some surprise allies will help even the odds!', 0, 0, '2022-05-03', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (25, 'Jacob''s Ladder (2024)', 7, 13, 30, '24.81x13.18x1.51 cm', NULL, '»Jacob’s Ladder« is a short story by F. Scott Fitzgerald, originally published in 1927. F. SCOTT FITZGERALD [1896-1940] was an American author, born in St. Paul, Minnesota. His legendary marriage to Zelda Montgomery, along with their acquaintances with notable figures such as Gertrude Stein and Ernest Hemingway, and their lifestyle in 1920s Paris, has become iconic. A master of the short story genre, it is logical that his most famous novel is also his shortest: The Great Gatsby [1925].', 0, 0, '2024-02-27', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (26, 'Magnetism (2024)', 7, 13, 28, '23.29x16.63x1.18 cm', NULL, '»Magnetism« is a short story by F. Scott Fitzgerald, originally published in 1928. F. SCOTT FITZGERALD [1896-1940] was an American author, born in St. Paul, Minnesota. His legendary marriage to Zelda Montgomery, along with their acquaintances with notable figures such as Gertrude Stein and Ernest Hemingway, and their lifestyle in 1920s Paris, has become iconic. A master of the short story genre, it is logical that his most famous novel is also his shortest: The Great Gatsby [1925].', 0, 0, '2024-02-27', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (27, 'More Than Just a House (2024)', 7, 13, 27, '23.27x16.68x2.17 cm', NULL, '»More Than Just a House« is a short story by F. Scott Fitzgerald, originally published in 1933. F. SCOTT FITZGERALD [1896-1940] was an American author, born in St. Paul, Minnesota. His legendary marriage to Zelda Montgomery, along with their acquaintances with notable figures such as Gertrude Stein and Ernest Hemingway, and their lifestyle in 1920s Paris, has become iconic. A master of the short story genre, it is logical that his most famous novel is also his shortest: The Great Gatsby [1925].', 0, 0, '2024-02-27', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (28, 'One Interne (2024)', 7, 13, 25, '23.02x16.31x1.29 cm', NULL, '»One Interne« is a short story by F. Scott Fitzgerald, originally published in 1932. F. SCOTT FITZGERALD [1896-1940] was an American author, born in St. Paul, Minnesota. His legendary marriage to Zelda Montgomery, along with their acquaintances with notable figures such as Gertrude Stein and Ernest Hemingway, and their lifestyle in 1920s Paris, has become iconic. A master of the short story genre, it is logical that his most famous novel is also his shortest: The Great Gatsby [1925].', 0, 0, '2024-02-27', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (29, 'The Complete Books of F. Scott Fitzgerald (all his 5 novels + all 4 short story collections published during his lifetime) (2023)', 7, 3, 2508, '24.85x13.72x2.35 cm', NULL, '"This carefully crafted ebook: ""The Complete Books of F. Scott Fitzgerald"" is formatted for your eReader with a functional and detailed table of contents. This eBook offers you the unique opportunity of exploring F. Scott Fitzgerald''s work in a manner never before possible in digital print. The edition includes every Fitzgerald story collection (published in his lifetime), short story, with poems and non-fiction. Novels: This Side of Paradise (New York: Charles Sons, 1920) The Beautiful and Damned (New York: Scribners, 1922) The Great Gatsby (New York: Scribners, 1925) Tender Is the Night (New York: Scribners, 1934) The Love of the Last Tycoon – originally The Last Tycoon – (New York: Scribners, unfinished, published posthumously, 1941) Short story collections: Flappers and Philosophers (New York: Scribners, 1921) Tales of the Jazz Age (New York: Scribners, 1922) All the Sad Young Men (New York: Scribners, 1926) Taps at Reveille (New York: Scribners, 1935) Francis Scott Key Fitzgerald (1896 – 1940) was an American writer of novels and short stories, whose works have been seen as evocative of the Jazz Age, a term he himself allegedly coined. He is regarded as one of the greatest twentieth century writers. Fitzgerald was of the self-styled ""Lost Generation,"" Americans born in the 1890s who came of age during World War I. He finished four novels, left a fifth unfinished, and wrote dozens of short stories that treat themes of youth, despair, and age. He was married to Zelda Fitzgerald."', 0, 0, '2023-12-30', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (30, 'Death Draws Five - A Wild Cards Novel (2021)', 8, 21, 267, '23.67x14.74x1.14 cm', NULL, 'Edited by bestselling author George R. R. Martin, in the next Wild Cards adventure we follow John Fortune, son of two of the most powerful and popular Aces the world has ever seen. In Death Draws Five, John Fortune''s card has finally turned. He''s an Ace! And proud of it . . . except that his new powers put him on a collision course with enemies he never knew he had. Is he the new messiah? Or the Anti-Christ? Or is he just a kid who''s in over his head and about to drown? It''s really quite simple. Mr. Nobody wants to do his job. The Midnight Angel wants to serve her Lord. Billy Ray, dying from boredom, wants some action. John Nighthawk wants to uncover the awful secret behind his mysterious power. Fortunato wants to rescue his son from the clutches of a cryptic Vatican office. John Fortune just wants to catch Siegfried and Ralph''s famous Vegas review. The problem is that all roads, whether they start in Turin, Italy, Las Vegas, Hokkaido, Japan, Jokertown, Snake Hill, the Short Cut, or Yazoo City, Mississippi, lead to Leo Barnett''s Peaceable Kingdom, where the difference between the Apocalypse and Peace on Earth is as thin as a razor''s edge and where Death himself awaits the final, terrible turn of the card. At the Publisher''s request, this title is being sold without Digital Rights Management Software (DRM) applied.', 0, 0, '2021-11-16', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (31, 'Joker Moon - A Wild Cards Novel (2021)', 8, 21, 368, '23.59x15.71x2.12 cm', NULL, 'In Joker Moon, the next Wild Cards adventure from series editor George R. R. Martin, we follow Aarti, the Moon Maid, who can astrally project herself onto the surface of the moon and paint projections across the lunarscape. Theodorus was a dreamer. As a child, he dreamt of airplanes, rockets, and outer space. When the wild card virus touched him and transformed him into a monstrous snail centaur weighing several tons, his boyhood dreams seemed out of reach, but a Witherspoon is not so easily defeated. Years and decades passed, and Theodorus grew to maturity and came into his fortune . . . but still his dream endured. But now when he looked upward into the night sky, he saw more than just the moon . . . he saw a joker homeland, a refuge where the outcast children of the wild card could make a place of their own, safe from hate and harm. An impossible dream, some said. Others, alarmed by the prospect, brought all their power to bear to oppose him. Theodorus persisted . . . . . . never dreaming that the Moon was already inhabited. And the Moon Maid did not want company. At the Publisher''s request, this title is being sold without Digital Rights Management Software (DRM) applied.', 0, 0, '2021-07-06', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (32, 'The Rise of the Dragon - An Illustrated History of the Targaryen Dynasty (2022)', 8, 6, 0, '23.36x16.96x2.47 cm', NULL, 'This lavish visual history--featuring over 150 new, full-colour illustrations--is a stunning introduction to House Targaryen, the iconic family at the heart of HBO''s Game of Thrones prequel series, House of the Dragon. For hundreds of years, the Targaryens sat the Iron Throne of Westeros while their dragons ruled the skies. The story of the only family of dragonlords to survive Valyria''s Doom is a tale of twisty politics, alliances and betrayals, and acts both noble and craven. The Rise of the Dragon chronicles the creation and rise of Targaryen power in Westeros, covering the history first told in George R. R. Martin''s epic Fire & Blood, from Aegon Targaryen''s conquest of Westeros through to the infamous Dance of the Dragons--the bloody civil war that nearly undid Targaryen rule for good. Packed with all-new artwork, the Targaryens--and their dragons--come vividly to life in this deluxe reference book. Perfect for fans steeped in the lore of Westeros, as well as those who first meet the Targaryens in the HBO series House of the Dragon, The Rise of the Dragon provides a must-have overview for anyone looking to learn more about the most powerful family in Westeros.', 0, 0, '2022-10-25', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (33, 'Voyaging, Volume One: The Plague Star (2023)', 8, 6, 194, '24.26x14.20x1.81 cm', NULL, 'Journey across the cosmos in George R. R. Martin''s beloved sci-fi universe, the Thousand Worlds, as a ragtag group of conspirators embark on a mysterious mission to gain unfathomable fame and fortune—if only they can survive.', 0, 0, '2023-12-07', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (34, 'Wild Cards XI: Dealer''s Choice - Book Three of the Rox Triad (2020)', 8, 21, 386, '23.32x13.16x1.28 cm', NULL, 'Soon to be a TV show! Rights to develop Wild Cards for TV have been acquired by Universal Cable Productions, the team that brought you The Magicians and Mr. Robot, with the co-editor of Wild Cards, Melinda Snodgrass as executive producer. After too many disastrous raids and military embarrassments, the Nats order a full-out, no-holds-barred blitzkrieg against Bloat and his genetic outcasts. The mission is clear: destroy Ellis Island, no survivors. As the final battle rages, the Turtle throws in the towel, Modular Man switches sides, Reflector faces defeat, Legion “dies”—and assassins reach Bloat’s chamber. This is it, folks. The final days of the Rox. The Wild Cards series explodes into apocalyptic battle action, edited by #1 New York Times bestselling author George R. R. Martin and Melinda M. Snodgrass, featuring the writing talents of Edward W. Bryant, Stephen Leigh, John Jos. Miller, George R. R. Martin and Walter Jon Williams. The Wild Cards Universe The Original Triad #1 Wild Cards #2 Aces High #3 Jokers Wild The Puppetman Quartet #4: Aces Abroad #5: Down and Dirty #6: Ace in the Hole #7: Dead Man’s Hand The Rox Triad #8: One-Eyed Jacks #9: Jokertown Shuffle #10: Double Solitaire #11: Dealer''s Choice #12: Turn of the Cards The Card Sharks Triad #13: Card Sharks #14: Marked Cards #15: Black Trump #16: Deuces Down #17: Death Draws Five The Committee Triad #18: Inside Straight #19: Busted Flush #20: Suicide Kings American Hero (ebook original) The Fort Freak Triad #21: Fort Freak #22: Lowball #23: High Stakes The American Triad #24: Mississippi Roll #25: Low Chicago #26: Texas Hold ''Em #27: Knaves Over Queens At the Publisher''s request, this title is being sold without Digital Rights Management Software (DRM) applied.', 0, 0, '2020-09-01', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (35, 'To Kill a Mockingbird - The stunning graphic novel adaptation (2018)', 9, 16, 290, '22.11x14.59x1.46 cm', NULL, 'A beautifully crafted graphic novel adaptation of Harper Lee’s beloved American classic, voted the #1 Great American Read 2018. ‘Shoot all the bluejays you want, if you can hit ‘em, but remember it’s a sin to kill a mockingbird.’ A haunting portrait of race and class, innocence and injustice, hypocrisy and heroism, tradition and transformation in the Deep South of the 1930s, Harper Lee’s To Kill a Mockingbird remains as important today as it was upon its initial publication in 1960, during the turbulent years of the Civil Rights movement. Now, this most beloved and acclaimed novel is reborn for a new age as a gorgeous graphic novel. Scout, Jem, Boo Radley, Atticus Finch and the small town of Maycomb, Alabama, are all captured in vivid and moving illustrations by artist Fred Fordham. Enduring in vision, Harper Lee’s timeless novel illuminates the complexities of human nature and the depths of the human heart with humour, unwavering honesty and a tender, nostalgic beauty. Lifetime admirers and new readers alike will be touched by this special visual edition.', 0, 0, '2018-10-30', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (36, 'To Kill a Mockingbird: A Graphic Novel - A Graphic Novel (2018)', 9, 5, 288, '22.89x13.61x2.10 cm', NULL, '"“This gorgeously rendered graphic-novel version provides a new perspective for old fans but also acts as an immersive introduction for youngsters as well as any adult who somehow missed out on the iconic story set in Maycomb, Alabama.”--USA Today A beautifully crafted graphic novel adaptation of Harper Lee’s beloved, Pulitzer Prize–winning American classic, voted America''s best-loved novel in PBS''s Great American Read. ""Shoot all the bluejays you want, if you can hit ‘em, but remember it’s a sin to kill a mockingbird."" A haunting portrait of race and class, innocence and injustice, hypocrisy and heroism, tradition and transformation in the Deep South of the 1930s, Harper Lee’s To Kill a Mockingbird remains as important today as it was upon its initial publication in 1960, during the turbulent years of the Civil Rights movement. Now, this most beloved and acclaimed novel is reborn for a new age as a gorgeous graphic novel. Scout, Jem, Boo Radley, Atticus Finch, and the small town of Maycomb, Alabama, are all captured in vivid and moving illustrations by artist Fred Fordham. Enduring in vision, Harper Lee’s timeless novel illuminates the complexities of human nature and the depths of the human heart with humor, unwavering honesty, and a tender, nostalgic beauty. Lifetime admirers and new readers alike will be touched by this special visual edition that joins the ranks of the graphic novel adaptations of A Wrinkle in Time and The Alchemist."', 0, 0, '2018-10-30', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (37, 'To Kill a Mockingbird: A Graphic Novel (2018)', 9, 5, 0, '22.06x15.90x1.59 cm', NULL, '"A beautifully crafted graphic novel adaptation of Harper Lee’s beloved, Pulitzer prize–winning American classic. ""Shoot all the bluejays you want, if you can hit ‘em, but remember it’s a sin to kill a mockingbird."" A haunting portrait of race and class, innocence and injustice, hypocrisy and heroism, tradition and transformation in the Deep South of the 1930s, Harper Lee’s To Kill a Mockingbird remains as important today as it was upon its initial publication in 1960, during the turbulent years of the Civil Rights movement. Now, this most beloved and acclaimed novel is reborn for a new age as a gorgeous graphic novel. Scout, Gem, Boo Radley, Atticus Finch, and the small town of Maycomb, Alabama, are all captured in vivid and moving illustrations by artist Fred Fordham. Enduring in vision, Harper Lee’s timeless novel illuminates the complexities of human nature and the depths of the human heart with humor, unwavering honesty, and a tender, nostalgic beauty. Lifetime admirers and new readers alike will be touched by this special visual edition that joins the ranks of the graphic novel adaptations of A Wrinkle in Time and The Alchemist."', 0, 0, '2018-10-30', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (38, 'Let''s Build (2020)', 10, 7, 57, '23.46x13.17x1.64 cm', NULL, 'The construction crew needs your help with their latest project! Tilt, tap, spin, and clap to demolish an old building, pour cement, and more in this sturdy, interactive picture book. Help the construction crew build a new park! They''ll need you to take charge of the wrecking ball, dig with an excavator, empty a dump truck, and plant some new greenery. Can you tilt the book to swing the wrecking ball? Use your strong arm to help dig? Point the animals to where the trees need planting? At the end of the day, celebrate your hard work with all the members of the critter crew!', 0, 0, '2020-01-21', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (39, 'I, Robot (2013)', 11, 6, 245, '22.08x14.42x2.47 cm', NULL, 'Voyager Classics - timeless masterworks of science fiction and fantasy. A beautiful clothbound edition of I, Robot, the classic collection of robot stories from the master of the genre.', 0, 0, '2012-11-21', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (40, 'I, Robot (2018)', 11, 6, 256, '24.31x14.27x1.36 cm', NULL, 'Earth is ruled by master-machines but the Three Laws of Robotics have been designed to ensure humans maintain the upper hand: 1) A robot may not injure a human being or allow a human being to come to harm 2) A robot must obey orders given to it by human beings except where such orders would conflict with the First Law. 3) A robot must protect its own existence as long as such protection does not conflict with the First or Second Law. But what happens when a rogue robot''s idea of what is good for society contravenes the Three Laws?', 0, 0, '2018-05-01', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (41, 'Magic (2023)', 11, 6, 298, '24.32x14.33x1.86 cm', NULL, 'A final collection of original short fantasy stories assembles previously uncollected tales, stories about the two-centimeter demon Azael, several fairy tales, and a humorous adventure about Batman''s old age from the grandmaster of science fiction.', 0, 0, '2023-11-09', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (42, 'The Caves of Steel (2023)', 11, 6, 265, '22.50x16.83x1.07 cm', NULL, 'Isaac Asimov’s Robot series – from the iconic collection I, Robot to four classic novels – contains some of the most influential works in the history of science fiction. Establishing and testing the Three Laws of Robotics, they continue to shape the understanding and design of artificial intelligence to this day.', 0, 0, '2023-08-31', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (43, 'The Complete Robot (2018)', 11, 6, 624, '24.09x16.50x2.15 cm', NULL, 'A collection of all of Isaac Asimov''s robot stories, including some which have never before appeared in book form.', 0, 0, '2018-05-09', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (44, 'The Complete Stories Volume II (2023)', 11, 6, 472, '22.02x15.48x1.93 cm', NULL, '"The second volume in an extraordinary collection published shortly after the author’s death. There are 23 science fiction stories, ranging from the very surprising heart-tugger ""The Ugly Little Boy"" to the overwhelming vision of ""Nightfall"". In these stories, Asimov''s vivid awareness of the potential of technology is translated into human dilemmas."', 0, 0, '2023-11-09', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (45, 'Harry Potter and the Prisoner of Azkaban (2020)', 13, 1, 180, '22.55x16.10x1.84 cm', NULL, 'Jim Kay''s dazzling depiction of J.K. Rowling''s wizarding world has won legions of fans since the first Illustrated Edition of the Harry Potter novels was published in hardback in 2015, becoming a bestseller around the world. This irresistible smaller-format paperback edition of Harry Potter and the Prisoner of Azkaban perfectly pairs J.K. Rowling''s storytelling genius with Jim Kay''s illustration wizardry, bringing the magic of Harry Potter to new readers with full-colour pictures and a handsome poster pull-out at the back of the book. This edition has been beautifully redesigned with selected illustration highlights, and is packed with breathtaking scenes and unforgettable characters - including Sirius Black, Remus Lupin and Professor Trelawney. The fully illustrated edition is still available in hardback.Fizzing with magic and brimming with humour, this inspired reimagining will captivate wizards and Muggles alike, as Harry, now in his third year at Hogwarts School of Witchcraft and Wizardry, faces Dementors, death omens and - of course - danger.', 0, 0, '2020-11-12', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (46, 'Harry Potter Slytherin House Editions Hardback Box Set (2021)', 13, 1, 3888, '24.53x15.24x1.35 cm', NULL, NULL, 0, 0, '2021-11-11', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (47, 'Harry Potter Slytherin House Editions Paperback Box Set (2022)', 13, 1, 3888, '24.93x14.76x1.61 cm', NULL, NULL, 0, 0, '2022-02-10', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (48, 'Harry Potter: A Magical Year -- The Illustrations of Jim Kay (2021)', 13, 18, 0, '23.68x15.22x1.08 cm', NULL, '"This ""book takes readers on an unforgettable journey through the seasons at Hogwarts. Jim Kay''s captivating illustrations, paired with much-loved quotations from J.K. Rowling''s Harry Potter novels -- one moment, anniversary, or memory for every day of the year -- bring to life all of the magic, beauty, and wonder of the wizarding world""--"', 0, 0, '2021-10-19', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (49, 'The Christmas Pig (2021)', 13, 18, 266, '24.98x14.73x1.36 cm', NULL, 'A heartwarming, page-turning adventure about one child''s love for his most treasured thing, and how far he will go to find it. A tale for the whole family to fall in love with, from one of the world’s greatest storytellers. One boy and his toy are about to change everything... Jack loves his childhood toy, Dur Pig. DP has always been there for him, through good and bad. Until one Christmas Eve something terrible happens -- DP is lost. But Christmas Eve is a night for miracles and lost causes, a night when all things can come to life... even toys. And Jack’s newest toy -- the Christmas Pig (DP’s replacement) – has a daring plan: Together they’ll embark on a magical journey to seek something lost, and to save the best friend Jack has ever known...', 0, 0, '2021-10-12', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (50, 'The Harry Potter Wizarding Almanac - The Official Magical Companion to J. K. Rowling''s Harry Potter Books (2023)', 13, 1, 0, '22.08x15.19x2.00 cm', NULL, 'The only official companion to the Harry Potter stories - the ultimate compendium of wizarding lists, charts, maps and all things magical! Whisk yourself away to Harry Potter''s wizarding world with this Whiz-bang of an illustrated companion. Discover magical places, study wandlore, encounter fantastic beasts and find out about the witches and wizards who lived. From the Sorting Hat to spell-casting, it''s all packed inside! This dazzling gift book brings together beloved characters, unforgettable moments and iconic locations from Harry Potter and the Philosopher''s Stone all the way through to The Deathly Hallows. It''s the ultimate magical miscellany, filled with facts and fun about the wizarding world, beautifully catalogued and brilliantly explored. Joyfully illustrated throughout in full colour by seven stunning artists and tingling with things to spot on every page, this is the ideal introduction to the Harry Potter stories for new readers and the perfect book for families to share. The Harry Potter Wizarding Almanac also features an astonishing level of detail that is sure to surprise and fascinate lifelong fans. From incredible cross-sections to magical maps and ingenious lists, lose yourself exploring Hogwarts and beyond ...', 0, 0, '2023-10-10', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (51, 'The Ickabog - A warm and witty fairy-tale adventure to entertain the whole family (2020)', 13, 4, 355, '24.05x16.50x1.93 cm', NULL, 'The Ickabog is coming... A mythical monster, a kingdom in peril, an adventure that will test two children''s bravery to the limit. Discover a brilliantly original fairy tale about the power of hope and friendship to triumph against all odds, from one of the world''s best storytellers. The kingdom of Cornucopia was once the happiest in the world. It had plenty of gold, a king with the finest moustaches you could possibly imagine, and butchers, bakers and cheesemongers whose exquisite foods made a person dance with delight when they ate them. Everything was perfect - except for the misty Marshlands to the north which, according to legend, were home to the monstrous Ickabog. Anyone sensible knew that the Ickabog was just a myth, to scare children into behaving. But the funny thing about myths is that sometimes they take on a life of their own. Could a myth unseat a beloved king? Could a myth bring a once happy country to its knees? Could a myth thrust two children into an adventure they didn''t ask for and never expected? If you''re feeling brave, step into the pages of this book to find out... A beautiful digital edition, brought to life with full-colour illustrations by the young winners of The Ickabog competition.', 0, 0, '2020-11-10', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (52, 'The Tales of Beedle the Bard - Illustrated Edition - A Magical Companion to the Harry Potter Stories (2022)', 13, 1, 0, '23.27x14.68x2.39 cm', NULL, 'A collection of fairy tales for young wizards and witches, with each story followed by observations on Wizarding history, personal reminiscences and information on the story''s key elements by Hogwarts headmaster, Albus Dumbledore.', 0, 0, '2022-03-31', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (53, 'The Adventures of Tom Bombadil (2024)', 14, 5, 0, '23.43x14.89x1.40 cm', NULL, '"A revised and expanded edition of Tolkien''s own Hobbit-inspired poetry, including previously unpublished poems and notes, and beautiful illustrations by Narnia artist Pauline Baynes. One of the most intriguing characters in The Lord of the Rings, the amusing and enigmatic Tom Bombadil also appears in verses said to have been written by Hobbits and preserved in the ""Red Book"" with stories of Bilbo and Frodo Baggins and their friends. The Adventures of Tom Bombadil collects these and other poems, mainly concerned with legends and jests of the Shire at the end of the Third Age. This edition includes earlier versions of some of Tolkien''s poems, a fragment of a prose story with Tom Bombadil, comprehensive notes by acclaimed Tolkien scholars Christina Scull and Wayne G. Hammond, and stunning illustrations by Narnia artist Pauline Baynes."', 0, 0, '2024-03-05', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (54, 'The Collected Poems of J. R. R. Tolkien (2024)', 14, 6, 0, '23.69x13.42x1.83 cm', NULL, 'World first publication of the collected poems of J.R.R. Tolkien spanning almost seven decades of the author’s life and presented in an elegant three-volume hardback boxed set.', 0, 0, '2024-03-08', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (55, 'The History of the Hobbit - One Volume Edition (2023)', 14, 5, 0, '24.55x16.47x1.44 cm', NULL, 'Brand new deluxe edition of this definitive companion to The Hobbit, quarter-bound, stamped in gold foil with a unique design inspired by J.R.R. Tolkien''s own artwork, featuring a ribbon marker and housed in a matching custom-built slipcase. The Hobbit was first published on 21 September 1937. Like its sequel, The Lord of the Rings, it is a story that ''grew in the telling'', and many characters and plot threads in the published text are quite different from the story J.R.R. Tolkien first wrote to read aloud to his young sons as one of their ''fireside reads''. Together in one volume, The History of the Hobbit presents the complete text of the unpublished manuscript of The Hobbit, accompanied by John Rateliff''s lively and informative account of how the book came to be written and published. Recording the numerous changes made to the story both before and after publication, he examines - chapter by chapter - why those changes were made and how they reflect Tolkien''s ever-growing concept of Middle-earth. As well as reproducing the original version of one of the world''s most popular novels - both on its own merits and as the foundation for The Lord of the Rings - this book includes many little-known illustrations and draft maps for The Hobbit by Tolkien himself. Also featured are extensive commentaries on the dates of composition, how Tolkien''s professional and early mythological writings influenced the story, the imaginary geography he created, and how Tolkien came to revise the book years after publication to accommodate events in The Lord of the Rings. Endorsed by Christopher Tolkien as a companion to his essential 12-volume The History of Middle-earth, this thoughtful and exhaustive examination of one of the most treasured stories in English literature offers fascinating new insights for those who have grown up with this enchanting tale, and will delight any who are about to enter Bilbo''s round door for the first time.', 0, 0, '2023-03-16', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (56, 'The History of the Hobbit (2023)', 14, 5, 0, '22.16x14.88x2.15 cm', NULL, '"In one volume for the first time, this revised and updated examination of how J.R.R. Tolkien came to write his original masterpiece The Hobbit includes his complete unpublished draft version of the story, together with notes and illustrations by Tolkien himself. The Hobbit was first published on September 21,1937. Like its sequel, The Lord of the Rings, it is a story that ""grew in the telling,"" and many characters and plot threads in the published text are quite different from the story J.R.R. Tolkien first wrote to read aloud to his young sons as one of their ""fireside reads."" Together in one volume, The History of the Hobbit presents the complete text of the unpublished manuscript of The Hobbit, accompanied by John Rateliff''s lively and informative account of how the book came to be written and published. Recording the numerous changes made to the story both before and after publication, he examines--chapter by chapter--why those changes were made and how they reflect Tolkien''s ever-growing concept of Middle-earth. As well as reproducing the original version of one of the world''s most popular novels--both on its own merits and as the foundation for The Lord of the Rings--this book includes many little-known illustrations and draft maps for The Hobbit by Tolkien himself. Also featured are extensive commentaries on the dates of composition, how Tolkien''s professional and early mythological writings influenced the story, the imaginary geography he created, and how Tolkien came to revise the book years after publication to accommodate events in The Lord of the Rings. Endorsed by Christopher Tolkien as a companion to his essential 12-volume The History of Middle-earth, this thoughtful and exhaustive examination of one of the most treasured stories in English literature offers fascinating new insights for those who have grown up with this enchanting tale, and will delight any who are about to enter Bilbo''s round door for the first time."', 0, 0, '2023-05-16', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (57, 'The Hobbit (2023)', 14, 5, 0, '22.25x16.41x1.69 cm', NULL, 'For the first time ever, a special enhanced edition of the enchanting prelude to The Lord of the Rings, illustrated throughout with over 50 sketches, drawings, paintings and maps by J. R. R. Tolkien himself and with the complete text printed in two colours. Bilbo Baggins is a hobbit who enjoys a comfortable, unambitious life, rarely travelling further than the pantry of his hobbit-hole in Bag End. But his contentment is disturbed when the wizard, Gandalf, and a company of thirteen dwarves arrive on his doorstep one day to whisk him away on an unexpected journey ''there and back again''. They have a plot to raid the treasure hoard of Smaug the Magnificent, a large and very dangerous dragon... Written for J.R.R. Tolkien''s own children, The Hobbit was published on 21 September 1937. With a beautiful cover design, a handful of black & white drawings and two maps by the author himself, the book became an instant success and was reprinted shortly afterwards with five colour plates. Tolkien''s own selection of finished paintings and drawings have become inseparable from his text, adorning editions of The Hobbit for more than 85 years. But the published art has afforded only a glimpse of Tolkien''s creative process, and many additional sketches, coloured drawings and maps - although exhibited and published elsewhere - have never appeared within the pages of The Hobbit. In this unique enhanced edition of Tolkien''s enchanting classic tale, the full panoply of his art is reproduced for the first time, presenting more than 50 illustrations to accompany Bilbo Baggins on his adventure ''there and back again''.', 0, 0, '2023-09-14', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (58, 'The Hobbit Collector''s Edition (2024)', 14, 5, 0, '23.47x15.88x1.48 cm', NULL, '"A special collectible hardcover edition of the best-selling classic, featuring the complete story with a sumptuous cover design, foil stamping, stained edges, and ribbon bookmark. Bilbo Baggins is a hobbit who enjoys a comfortable, unambitious life, rarely travelling further than the pantry of his hobbit-hole in Bag End. But his contentment is disturbed when the wizard, Gandalf, and a company of thirteen dwarves arrive on his doorstep one day to whisk him away on an unexpected journey ""there and back again."" They have a plot to raid the treasure hoard of Smaug the Magnificent, a large and very dangerous dragon. . . The prelude to The Lord of the Rings, The Hobbit has sold many millions of copies since its publication in 1937, establishing itself as one of the most beloved and influential books of the twentieth century. This elegant hardcover - now available for the first time in the United States - is one of five Collector''s Editions of Tolkien''s most beloved works, and an essential piece of any Tolkien reader''s library."', 0, 0, '2024-03-22', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (59, 'The Hobbit Deluxe Illustrated Edition (2024)', 14, 5, 0, '23.84x16.10x2.01 cm', NULL, '"For the first time ever, a beautiful slipcased edition of the enchanting prelude to The Lord of the Rings, illustrated throughout with over 50 sketches, drawings, paintings, and maps by J.R.R. Tolkien himself, with the complete text printed in two colors and with many bonus features unique to this edition. Bilbo Baggins is a hobbit who enjoys a comfortable, unambitious life, rarely travelling further than the pantry of his hobbit-hole in Bag End. But his contentment is disturbed when the wizard, Gandalf, and a company of thirteen dwarves arrive on his doorstep one day to whisk him away on an unexpected journey ""there and back again."" They have a plot to raid the treasure hoard of Smaug the Magnificent, a large and very dangerous dragon... Written for J.R.R. Tolkien''s own children, The Hobbit was published on 21 September 1937. With a beautiful cover design, a handful of black & white drawings and two maps by the author himself, the book became an instant success and was reprinted shortly afterwards with five color plates. Tolkien''s own selection of finished paintings and drawings have become inseparable from his text, adorning editions of The Hobbit for more than 85 years. But the published art has afforded only a glimpse of Tolkien''s creative process, and many additional sketches, colored drawings, and maps--although exhibited and published elsewhere--have never appeared within the pages of The Hobbit. In this unique enhanced edition of Tolkien''s enchanting classic tale, the full panoply of his art is reproduced for the first time, presenting more than 50 illustrations to accompany Bilbo Baggins on his adventure ""there and back again."" Unique to this edition are two poster-size, fold-out maps revealing all the detail of Thror''s Map and Wilderland, an illustrated 88-page booklet, and a printed art card reproducing Tolkien''s original dustjacket painting. It is additionally quarterbound in green leather, with raised ribs on the spine, stamped in three foils on black cloth boards, and housed in a custom-built clothbound slipcase. The pages are edged in gold and include a ribbon marker."', 0, 0, '2024-03-11', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (60, 'The Hobbit Illustrated by the Author (2023)', 14, 5, 0, '23.33x14.98x2.43 cm', NULL, 'For the first time ever, a special enhanced edition of the enchanting prelude to The Lord of the Rings, illustrated throughout with over 50 sketches, drawings, paintings and maps by J.R.R. Tolkien himself and with the complete text printed in two colors. Bilbo Baggins is a hobbit who enjoys a comfortable, unambitious life, rarely travelling further than the pantry of his hobbit-hole in Bag End. But his contentment is disturbed when the wizard, Gandalf, and a company of thirteen dwarves arrive on his doorstep one day to whisk him away on an unexpected journey ''there and back again.'' They have a plot to raid the treasure hoard of Smaug the Magnificent, a large and very dangerous dragon . . . Written for J.R.R. Tolkien''s own children, The Hobbit was published on 21 September 1937. With a beautiful cover design, a handful of black & white drawings and two maps by the author himself, the book became an instant success and was reprinted shortly afterwards with five color plates. Tolkien''s own selection of finished paintings and drawings have become inseparable from his text, adorning editions of The Hobbit for more than 85 years. But the published art has afforded only a glimpse of Tolkien''s creative process, and many additional sketches, colored drawings and maps - although exhibited and published elsewhere - have never appeared within the pages of The Hobbit. In this unique enhanced edition of Tolkien''s enchanting classic tale, the full panoply of his art is reproduced for the first time, presenting more than 50 illustrations to accompany Bilbo Baggins on his adventure ''there and back again.''', 0, 0, '2023-09-19', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (61, 'The Hobbit: Illustrated by the Author (2023)', 14, 6, 387, '24.13x13.50x1.21 cm', NULL, 'For the first time ever, a special enhanced edition of the enchanting prelude to The Lord of the Rings, illustrated throughout with 50 sketches, drawings, paintings and maps by J.R.R. Tolkien himself.', 0, 0, '2023-09-14', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (62, 'The Letters of J. R. R. Tolkien (2023)', 14, 5, 0, '22.70x16.79x1.27 cm', NULL, 'The comprehensive collection of letters spanning the adult life of one of the world''s greatest storytellers, now revised and expanded to include more than 150 previously unseen letters, with revealing new insights into The Hobbit, The Lord of the Rings and The Silmarillion. J.R.R. Tolkien, creator of the languages and history of Middle-earth as recorded in The Hobbit, The Lord of the Rings and The Silmarillion, was one of the most prolific letter-writers of this century. Over the years he wrote a mass of letters - to his publishers, to members of his family, to friends, and to ''fans'' of his books - which often reveal the inner workings of his mind, and which record the history of composition of his works and his reaction to subsequent events. A selection from Tolkien''s correspondence, collected and edited by Tolkien''s official biographer, Humphrey Carpenter, and assisted by Christopher Tolkien, was published in 1981. It presented, in Tolkien''s own words, a highly detailed portrait of the man in his many aspects: storyteller, scholar, Catholic, parent, friend, and observer of the world around him. In this revised and expanded edition of The Letters of J.R.R. Tolkien, it has been possible to go back to the editors'' original typescripts and notes, restoring more than 150 letters that were excised purely to achieve what was then deemed a ''publishable length'', and present the book as originally intended. Enthusiasts for his writings will find much that is new, for the letters not only include fresh information about Middle-earth, such as Tolkien''s own plot summary of the entirety of The Lord of the Rings and a vision for publishing his ''Tales of the Three Ages'', but also many insights into the man and his world. In addition, this new selection will entertain anyone who appreciates the art of letter-writing, of which J.R.R. Tolkien was a master.', 0, 0, '2023-11-09', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (63, 'The Letters of J. R. R. Tolkien: Revised and Expanded edition (2023)', 14, 6, 601, '23.27x13.33x2.18 cm', NULL, 'The comprehensive collection of letters spanning the adult life of one of the world’s greatest storytellers, now revised and expanded to include more than 150 previously unseen letters, with revealing new insights into The Hobbit, The Lord of the Rings and The Silmarillion.', 0, 0, '2023-11-09', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (64, 'The Lord of the Rings (2021)', 14, 5, 1248, '22.59x15.52x1.17 cm', NULL, 'For the first time ever, a very special edition of the classic masterpiece, with the complete text and illustrated throughout by the author himself.', 0, 0, '2021-10-14', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (65, 'The Lord of the Rings (2022)', 14, 5, 0, '23.25x15.74x1.31 cm', NULL, 'Begin your journey into Middle-earth. A New Legend Begins on Prime Video, in The Lord of the Rings: The Rings of Power. This one-volume, paperback edition includes The Fellowship of the Ring, The Two Towers, and The Return of the King, together with the Appendices in full. Sauron, the Dark Lord, has gathered to him all the Rings of Power - the means by which he intends to rule Middle-earth. All he lacks in his plans for dominion is the One Ring - the ring that rules them all - which has fallen into the hands of the hobbit, Bilbo Baggins. In a sleepy village in the Shire, young Frodo Baggins finds himself faced with an immense task, as the Ring is entrusted to his care. He must leave his home and make a perilous journey across the realms of Middle-earth to the Crack of Doom, deep inside the territories of the Dark Lord. There he must destroy the Ring forever and foil the Dark Lord in his evil purpose.', 0, 0, '2022-08-18', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (66, 'The Lord of the Rings [Illustrated Edition] (2021)', 14, 5, 0, '23.85x16.18x2.07 cm', NULL, 'For the first time ever, a very special edition of the classic masterpiece, illustrated throughout in colour by the author himself and with the complete text printed in two colours. Since it was first published in 1954, The Lord of the Rings has been a book people have treasured. Steeped in unrivalled magic and otherworldliness, its sweeping fantasy and epic adventure has touched the hearts of young and old alike. Over 150 million copies of its many editions have been sold around the world, and occasional collectors'' editions become prized and valuable items of publishing. This one-volume hardback edition contains the complete text, fully corrected and reset, which is printed in red and black and features, for the very first time, thirty colour illustrations, maps and sketches drawn by Tolkien himself as he composed this epic work. These include the pages from the Book of Mazarbul, marvellous facsimiles created by Tolkien to accompany the famous ''Bridge of Khazad-dum'' chapter. Also appearing are two removable fold-out maps drawn by Christopher Tolkien revealing all the detail of Middle-earth. Sympathetically packaged to reflect the classic look of the first edition, this new edition of the bestselling hardback will prove irresistible to collectors and new fans alike.', 0, 0, '2021-10-14', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (67, 'The Lord of the Rings Illustrated (2021)', 14, 5, 1218, '23.21x16.62x1.84 cm', NULL, 'For the first time ever, a very special edition of the J.R.R. Tolkien''s classic masterpiece, The Lord of the Rings, illustrated throughout in color by the author himself and with the complete text printed in two colors, plus sprayed edges and a ribbon bookmark. Since it was first published in 1954, The Lord of the Rings has been a book people have treasured. Steeped in unrivaled magic and otherworldliness, its sweeping fantasy and epic adventure has touched the hearts of young and old alike. More than 150 million copies of its many editions have been sold around the world, and occasional collectors’ editions become prized and valuable items of publishing. This one-volume, jacketed hardcover edition contains the complete text, fully corrected and reset, which is printed in red and black and features, for the very first time, thirty color illustrations, maps and sketches drawn by Tolkien himself as he composed this epic work. These include the pages from the Book of Mazarbul, marvelous facsimiles created by Tolkien to accompany the famous ‘Bridge of Khazad-dum’ chapter. Also appearing are two removable fold-out maps drawn by Christopher Tolkien revealing all the detail of Middle-earth. Sympathetically packaged to reflect the classic look of the first edition, this new edition of the bestselling hardback will prove irresistible to collectors and new fans alike.', 0, 0, '2021-11-02', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (68, 'The Nature Of Middle-Earth (2021)', 14, 5, 467, '24.84x14.74x2.31 cm', NULL, 'The first ever publication of J.R.R. Tolkien’s final writings on Middle-earth, covering a wide range of subjects and perfect for those who have read and enjoyed The Silmarillion, The Lord of the Rings, Unfinished Tales, and The History of Middle-earth, and want to learn more about Tolkien’s magnificent world. It is well known that J.R.R. Tolkien published The Hobbit in 1937 and The Lord of the Rings in 1954–5. What may be less known is that he continued to write about Middle-earth in the decades that followed, right up until the years before his death in 1973. For him, Middle-earth was part of an entire world to be explored, and the writings in The Nature of Middle-earth reveal the journeys that he took as he sought to better understand his unique creation. From sweeping themes as profound as Elvish immortality and reincarnation, and the Powers of the Valar, to the more earth-bound subjects of the lands and beasts of Númenor, the geography of the Rivers and Beacon-hills of Gondor, and even who had beards! This new collection, which has been edited by Carl F. Hostetter, one of the world’s leading Tolkien experts, is a veritable treasure-trove offering readers a chance to peer over Professor Tolkien’s shoulder at the very moment of discovery: and on every page, Middle-earth is once again brought to extraordinary life.', 0, 0, '2021-09-02', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (69, 'The Peoples of Middle-earth (2023)', 14, 5, 441, '23.56x16.10x1.20 cm', NULL, '"Throughout this vast and intricate mythology, says Publishers Weekly, ""one marvels anew at the depth, breadth, and persistence of J.R.R. Tolkien''s labor. No one sympathetic to his aims, the invention of a secondary universe, will want to miss this chance to be present at the creation."" In this capstone to that creation, we find the chronology of Middle-earth''s later Ages, the Hobbit genealogies, and the Western language or Common Speech. These early essays show that Tolkien''s fertile imagination was at work on Middle-earth''s Second and Third Ages long before he explored them in the Appendices to The Lord of the Rings . Here too are valuable writings from Tolkien''s last years: "" The New Shadow,"" in Gondor of the Fourth Age, and"" Tal-elmar,"" the tale of the coming of the Nsmen-rean ships."', 0, 0, '2023-06-13', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (70, 'The Return of the King (the Lord of the Rings, Book 3) (2022)', 14, 5, 0, '24.11x14.38x2.00 cm', NULL, 'Special clothbound collector''s hardback edition of the final part of J.R.R. Tolkien''s epic masterpiece, The Lord of the Rings. This edition features the complete story with a unique cover design and the iconic maps appearing in red and black as endpapers.', 0, 0, '2022-08-18', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (71, 'The Silmarillion (2022)', 14, 6, 376, '23.66x15.12x1.89 cm', NULL, 'For the first time ever, a very special edition of the forerunner to The Lord of the Rings, illustrated throughout in colour by J.R.R. Tolkien himself and with the complete text.', 0, 0, '2022-11-10', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (72, 'The Silmarillion Collector''s Edition (2024)', 14, 5, 0, '24.18x15.73x1.60 cm', NULL, 'A special collectible hardcover edition of the precursor to J.R.R. Tolkien''s epic masterpiece, The Lord of the Rings, featuring the complete work with a sumptuous cover design, foil stamping, stained edges, and ribbon bookmark. The Silmarillion is an account of the Elder Days, of the First Age of Tolkien''s World. It is the ancient drama to which the characters in The Lord of the Rings look back, and in whose events some of them, such as Elrond and Galadriel, took part. The tales of The Silmarillion are set in an age when Morgoth, the first Dark Lord, dwelt in Middle-Earth, and the High Elves made war upon him for the recovery of the Silmarils, the jewels containing the pure light of Valinor. This special hardcover edition of the work includes a unique, foil-stamped cover that illustrates Telperion and Laurelin, the Two Trees of Valinor, and the three Silmarils. Available in the United States for the first time, it is one of five Collector''s Editions of Tolkien''s most beloved works, and an essential piece of any Tolkien reader''s library.', 0, 0, '2024-03-23', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (73, 'The Two Towers (the Lord of the Rings, Book 2) (2022)', 14, 5, 0, '23.52x14.62x1.18 cm', NULL, NULL, 0, 0, '2022-08-18', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (74, 'The Two Towers [Tv Tie-In]: The Lord of the Rings Part Two (2022)', 14, 5, 352, '24.70x15.28x1.80 cm', NULL, '"Begin your journey into Middle-earth. The inspiration for the upcoming original series on Prime Video, The Lord of the Rings: The Rings of Power. The Two Towers is the second part of J.R.R. Tolkien''s epic adventure The Lord of the Rings. One Ring to rule them all, One Ring to find them, One Ring to bring them all and in the darkness bind them. Frodo and his Companions of the Ring have been beset by danger during their quest to prevent the Ruling Ring from falling into the hands of the Dark Lord by destroying it in the Cracks of Doom. They have lost the wizard, Gandalf, in a battle in the Mines of Moria. And Boromir, seduced by the power of the Ring, tried to seize it by force. While Frodo and Sam made their escape, the rest of the company was attacked by Orcs. Now they continue the journey alone down the great River Anduin--alone, that is, save for the mysterious creeping figure that follows wherever they go. ""Among the greatest works of imaginative fiction of the twentieth century. The book presents us with the richest profusion of new lands and creatures, from the beauty of Lothlórien to the horror of Mordor.""-Sunday Telegraph"', 0, 0, '2022-07-05', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (75, 'The War Of The Ring - The History of the Lord of the Rings, Part Three (2021)', 14, 5, 414, '23.10x15.37x1.71 cm', NULL, 'The third part of The History of The Lord of the Rings, J.R.R. Tolkien''s The War Of The Ring is an enthralling account of the writing of the Book of the Century, which contains many additional scenes and includes the unpublished Epilogue in its entirety. The War of the Ring takes up the story of The Lord of the Rings with the Battle of Helm’s Deep and the drowning of Isengard by the Ents, continues with the journey of Frodo, Sam and Gollum to the Pass of Cirith Ungol, describes the war in Gondor, and ends with the parley between Gandalf and the ambassador of the Dark Lord before the Black Gate of Mordor. The book is illustrated with plans and drawings of the changing conceptions of Orthanc, Dunharrow, Minas Tirith and the tunnels of Shelob’s Lair.', 0, 0, '2021-09-07', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (76, 'Happy Ending - The Fisher Brothers #4 (2018)', 15, 10, 0, '22.83x13.71x1.90 cm', NULL, 'What happens after the books we love end? New York Times Bestselling author J. Sterling takes you home with the Fisher Brothers for one last heart-racing story! Hold onto your hats and enjoy one last drink with the boys. Weddings, proposals, and babies...oh my! What happens after you''ve found your happily-ever-after? The Fisher boys learn that life is unpredictable and nothing is certain. Will they get to keep everything they''ve worked so hard to achieve, or will they lose it all? Come join the brothers for one last round at the bar and let''s cheers to a happy ending! The Fisher Brothers is a series of Stand-Alone Romance Novels that do not have to be read in order to be enjoyed. Happy Ending however, should not be read before reading Adios Pantalones, unless you like ruining surprises and reading the last page of a novel first. LOL No Bad Days- Book #1 Guy Hater- Book #2 Adios Pantalones- Book #3 Happy Ending- Book #4', 0, 0, '2018-09-06', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (77, 'Kissing my Co-worker (2021)', 15, 10, 57, '22.85x15.45x1.95 cm', NULL, 'I''ve had a crush on my co-worker Declan since the day I started working at Rockline Studios. Trust me, if you could see this man, you''d have a crush on him too. It''s been two years since my first day of work... two years since my head has been filled with fantasies and daydreams about the things I could do to him. Two years of.... SITTING BY AND DOING NOTHING because dating within the office is forbidden, frowned upon, something we''re not supposed to do. The night of Rockline''s infamous New Year''s Eve party changes everything. Will my new years wish finally come true?', 0, 0, '2021-12-16', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (78, 'No Bad Days - A New Adult Romance (2017)', 15, 10, 0, '24.63x14.67x1.01 cm', NULL, NULL, 0, 0, '2017-01-09', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (79, 'The Game Changer (2020)', 15, 10, 317, '23.36x14.04x1.64 cm', NULL, 'Jack appeared at my door last night after six months of no communication wearing a Mets jersey and holding a dozen red roses. He told me he was sorry, that he loved me, and that he would earn my trust again. It took everything in me to not fall apart at the mere sight of him. I wanted to take him back into my life, but I needed to know that this time it would be forever… In J. Sterling’s highly anticipated follow-up to her USA Today bestselling novel The Perfect Game, Jack and Cassie quickly realize that their new lifestyle can often be cruel and unforgiving. Their happiness is put to the test as the past is never truly far behind. How do you stay together when the world''s trying to tear you apart?', 0, 0, '2020-04-21', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (80, 'The Ninth Inning (2020)', 15, 10, 157, '23.95x15.99x2.18 cm', NULL, '"New York Times Bestselling author J. Sterling is BACK with a brand new baseball series! ""This is sports romance at its best! I was hooked from the first page! Cole & Christina''s romance is my new favorite."" - Claire Contreras, NYT Bestselling Author ""The Ninth Inning is a home run! The first book in the Boys of Baseball is classic Sterling- hot baseball players, college antics, lots of angst and a crazy-against-all-odds love!"" - Jillian Dodd, USA Today Bestselling Author Cole Anders is in his last season at Fullton State. If he doesn’t get drafted this year, he’ll be forced to hang up his cleats for good. It''s not something he’s ready to do. To prove he’s serious about his final season, he’s given up girls. No more casual hookups, dates or one-night stands. But there''s one girl who has always refused to give up on him. One girl who has been there since the start of Freshman year. One girl he stupidly assumed would always be waiting for him when he got off the field, no matter how long it took. Christina''s had enough of Cole pushing her aside for baseball. She’s grown tired of waiting for him to see what she’s known since they were freshmen… that they are good together. She’s finally moved on- for good this time. Cole has other ideas, but she refuses to cave. It’s not her problem if he wants her now. Too little too late. There comes a point in your life when you have to stop the incessant merry-go-round and just get off the ride, right? Try telling that to Cole."', 0, 0, '2020-04-10', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (81, 'Jane Austen''s Pride and Prejudice - A Book-to-Table Classic (2018)', 16, 16, 338, '23.42x14.87x2.09 cm', NULL, 'Puffin Plated: A Book-to-Table Reading Experience A deluxe, full-color hardback edition of the perennial Jane Austen classic featuring a selection of recipes for tea-time treats by the one and only Martha Stewart! Have your book and eat it, too, with this clever edition of a classic novel, featuring delicious recipes from celebrity chefs. In this edition of Jane Austen''s regency classic Pride and Prejudice, plan a fancy tea party or book club gathering with recipes for sweet confections and pastries. From maple glazed scones and delicate sugar and spice cake, to berry tartlets and French macaroons. Bring your friends and family together with a good meal and a good book! Book includes full, unabridged text of Jane Austen''s Pride and Prejudice, interspersed with recipes, food photography, and special food artwork.', 0, 0, '2018-10-16', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (82, 'Mansfield Park - Jane Austen Classic (annotated) (2021)', 16, 8, 434, '23.78x15.63x1.20 cm', NULL, 'Mansfield Park: Annotated by Jane AustenAdopted into the Bertram family by aunt and uncle in law Sir Thomas Bertram, Fanny Price grows up a compliant outcast among her cousins in the not used to in Mansfield Park. Not long after Sir Thomas absents himself on Antigua''s domain business, Mary Crawford and her sibling Henry show up at Mansfield, taking with them London allure and the tempting preference for flirtation and theatre that precipitates a crisis.', 0, 0, '2021-03-24', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (83, 'Cannery Row (2020)', 17, 16, 181, '23.75x13.51x2.24 cm', NULL, 'In this tough yet charming portrait of people on the margins of society, Steinbeck focuses on the acceptance of life as it is—both the exuberance of community and the loneliness of the individual. Drawing on his memories of friends in Monterey, California, he interweaves the stories of Lee, Doc, and Mack, the inhabitants of Cannery Row. What results is a procession of linked vignettes and a novel that is at once Steinbeck’s most humorous and poignant works, filled with human warmth, camaraderie, and love. Penguin Random House Canada is proud to bring you classic works of literature in e-book form, with the highest quality production values. Find more today and rediscover books you never knew you loved.', 0, 0, '2020-02-11', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (84, 'Of Mice and Men - A Play (2014)', 17, 16, 178, '22.90x13.31x2.47 cm', NULL, 'A theatrical adaptation of one of John Steinbecks short novels.', 0, 0, '2014-05-07', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (85, 'Of Mice and Men (2020)', 17, 16, 105, '24.01x15.21x1.52 cm', NULL, 'George and Lennie are an unlikely pair: George is small and quick, and Lennie is a man of tremendous size and simple mind. But together they’ve formed a family, rallying against seclusion and alienation. As laborers in California’s vegetable fields in the Great Depression, they find work where they can, but they have big dreams of owning land and a shack they can call their own. So when they land jobs on a ranch in the Salinas Valley, their big dreams suddenly seem within reach. But George cannot guard the childlike Lennie from life’s provocations, nor can he predict the consequences of Lennie’s unswerving obedience to what George has taught him. A tragic and moving story of friendship, loneliness, and the dispossessed, Of Mice and Men is classic John Steinbeck—heart wrenching and remarkable. Penguin Random House Canada is proud to bring you classic works of literature in e-book form, with the highest quality production values. Find more today and rediscover books you never knew you loved.', 0, 0, '2020-02-11', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (86, 'Of Mice and Men (2021)', 17, 3, 91, '24.44x13.10x2.47 cm', NULL, '"""Of Mice and Men"" by John Steinbeck. Published by Good Press. Good Press publishes a wide range of titles that encompasses every genre. From well-known classics & literary fiction and non-fiction to forgotten−or yet undiscovered gems−of world literature, we issue the books that need to be read. Each Good Press edition has been meticulously edited and formatted to boost readability for all e-readers and devices. Our goal is to produce eBooks that are user-friendly and accessible to everyone in a high-quality digital format."', 0, 0, '2021-08-30', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (87, 'Of Mice and Men: Teacher''s Deluxe Edition (2013)', 17, 16, 122, '24.47x13.37x1.41 cm', NULL, 'Penguin Classics presents John Steinbeck’s classic tale as an eBook enhanced with ten exclusive video clips featuring students responses, questions for classroom discussions, and an American Dream assignment Nobel Prize-winner John Steinbeck’s Of Mice and Men remains one of America''s most widely read and taught novels. An unlikely pair, George and Lennie, two migrant workers in California during the Great Depression, grasp for their American Dream. Laborers in California''s dusty vegetable fields, they hustle work when they can, living a hand-to-mouth existence. For George and Lennie have a plan: to own an acre of land and a shack they can call their own. When they land jobs on a ranch in the Salinas Valley, the fulfillment of their dream seems to be within their grasp. But even George cannot guard Lennie from the provocations, nor predict the consequences of Lennie''s unswerving obedience to the things George taught him. Of Mice and Men: Teacher’s Edition includes the following: • An introduction and suggested further reading by Susan Shillinglaw, a professor of English at San Jose State University and Scholar-in-Residence at the National Steinbeck Center in Salinas • The poem “To a Mouse, On Turning Her Up in Her Nest with the Plough, November 1785” by Robert Burns (the original source of Steinbeck’s title Of Mice and Men) • The 1962 Nobel Banquet Speech by John Steinbeck • An exclusive audio interview with award-winning actor James Earl Jones on his stage performances in Of Mice and Men • Ten exclusive videos of students on major themes from the novel tied to group discussion questions included in the eBook, and an American Dream assignment, for the ultimate educational experience', 0, 0, '2013-01-03', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (88, 'The Grapes of Wrath - 75th Anniversary Edition (2014)', 17, 16, 497, '23.10x16.57x2.07 cm', NULL, 'April 2014 marks the 75th anniversary of the first Viking hardcover publication of Steinbeck’s crowning literary achievement First published in 1939, Steinbeck’s Pulitzer Prize–winning epic of the Great Depression chronicles the Dust Bowl migration of the 1930s and tells the story of one Oklahoma farm family, the Joads, driven from their homestead and forced to travel west to the promised land of California. Out of their trials and their repeated collisions against the hard realities of an America divided into haves and have-nots evolves a drama that is intensely human yet majestic in its scale and moral vision, elemental yet plainspoken, tragic but ultimately stirring in its human dignity. A portrait of the conflict between the powerful and the powerless, of one man’s fierce reaction to injustice, and of one woman’s stoical strength, the novel captures the horrors of the Great Depression and probes the very nature of equality and justice in America. As Don DeLillo has claimed, Steinbeck “shaped a geography of conscience” with this novel where “there is something at stake in every sentence.” Beyond that—for emotional urgency, evocative power, sustained impact, prophetic reach, and continued controversy—The Grapes of Wrath is perhaps the most American of American classics. To commemorate the book''s 75th anniversary, this volume is modeled on the first edition, featuring the original cover illustration by Elmer Hader and specially designed endpapers by Michael Schwab.', 0, 0, '2014-04-10', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (89, 'The Grapes of Wrath (2020)', 17, 16, 455, '24.25x13.51x2.49 cm', NULL, 'An epic human drama depicting the devastating effects of the Great Depression, The Grapes of Wrath won both the National Book Award and the Pulitzer Prize, cementing its place as the most American of American classics. First published in 1939, Steinbeck’s novel chronicles the Dust Bowl migration of the 1930s and tells the story of one Oklahoma farm family, the Joads, driven from their homestead and forced to travel west to the promised land of California. Out of their repeated collisions with hard realities of an America divided into the Haves and Have-Nots evolves a drama intensely human and yet magnificent in scale and moral. An evocative portrait of the conflict between powerful and powerless, of one man’s fierce reaction to injustice, and of one woman’s stoical strength, The Grapes of Wrath probes into the very nature of equality and justice in America. Penguin Random House Canada is proud to bring you classic works of literature in e-book form, with the highest quality production values. Find more today and rediscover books you never knew you loved.', 0, 0, '2020-02-11', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (90, 'The Grapes of Wrath 75th Anniversary Edition (Limited edition) (2014)', 17, 16, 496, '22.78x14.39x1.87 cm', NULL, 'April 2014 marks the 75th anniversary of the first Viking hardcover publication of Steinbeck’s crowning literary achievement First published in 1939, Steinbeck’s Pulitzer Prize–winning epic of the Great Depression chronicles the Dust Bowl migration of the 1930s and tells the story of one Oklahoma farm family, the Joads, driven from their homestead and forced to travel west to the promised land of California. Out of their trials and their repeated collisions against the hard realities of an America divided into haves and have-nots evolves a drama that is intensely human yet majestic in its scale and moral vision, elemental yet plainspoken, tragic but ultimately stirring in its human dignity. A portrait of the conflict between the powerful and the powerless, of one man’s fierce reaction to injustice, and of one woman’s stoical strength, the novel captures the horrors of the Great Depression and probes the very nature of equality and justice in America. As Don DeLillo has claimed, Steinbeck “shaped a geography of conscience” with this novel where “there is something at stake in every sentence.” Beyond that—for emotional urgency, evocative power, sustained impact, prophetic reach, and continued controversy—The Grapes of Wrath is perhaps the most American of American classics. This is a commemorative edition specially designed to celebrate the 75th anniversary of The Grapes of Wrath. It features color endpapers and a leather case with black foil stamping specially designed by Michael Schwab, as well as a gilded top and a California Poppy-orange ribbon.', 0, 0, '2014-04-10', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (91, 'The Long Valley (2022)', 17, 23, 298, '23.23x14.91x2.16 cm', NULL, 'This collection of 12 classic short stories serves as a perfect introduction to John Steinbeck''s work. Set in the Salinas Valley in California, where everyday people farm the land and strive to better themselves, these stories turn on many key themes that Steinbeck explored throughout his career: Included are such classics as The Red Pony, The Murderer, and The Chrysanthemums.', 0, 0, '2022-06-07', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (92, 'The Portable Steinbeck (2012)', 17, 16, 642, '24.87x14.44x1.06 cm', NULL, 'This is a collection of excerpts of the work of John Steinbeck, along with the complete texts of ''The Red Pony'' and ''Of Mice and Men''.', 0, 0, '2012-09-25', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (93, 'Frieren: Beyond Journey''s End, Vol. 1 (2021)', 18, 22, 192, '23.89x14.70x2.33 cm', 'Misa “Japanese Ammo”', 'The adventure is over but life goes on for an elf mage just beginning to learn what living is all about. Elf mage Frieren and her courageous fellow adventurers have defeated the Demon King and brought peace to the land. With the great struggle over, they all go their separate ways to live a quiet life. But as an elf, Frieren, nearly immortal, will long outlive the rest of her former party. How will she come to terms with the mortality of her friends? How can she find fulfillment in her own life, and can she learn to understand what life means to the humans around her? Frieren begins a new journey to find the answer. Frieren, Himmel, Heiter and Eisen celebrate their victory by watching the Era meteor shower, an event which occurs every 50 years. After casually promising to meet them when it happens again, Frieren leaves to study magic. The years go by and the day comes when Frieren must attend the funeral of one of her comrades, confronting her with her own near immortality. When she realizes she barely knew the man she fought beside for ten years, she sets out to learn more about people, and carry out the last wishes of her friend--to decipher the secret magic of immortality.', 0, 0, '2021-11-09', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (94, 'Frieren: Beyond Journey’s End, Vol. 2 (2022)', 18, 22, 200, '22.85x15.92x1.07 cm', 'Misa “Japanese Ammo”', 'At Eisen’s urging, Frieren and her apprentice Fern head north seeking the land where heroes’ souls are said to rest, which also happens to be the location of the Demon King’s castle. Along the way, they meet Eisen’s apprentice, whose fighting skills may come in handy—though the Demon King is long gone, his surviving minions have unfinished business with Frieren! -- VIZ Media', 0, 0, '2022-01-11', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (95, 'Frieren: Beyond Journey''s End, Vol. 3 (2022)', 18, 22, 200, '22.61x14.82x1.96 cm', 'Misa “Japanese Ammo”', 'The adventure is over but life goes on for an elf mage just beginning to learn what living is all about. Elf mage Frieren and her courageous fellow adventurers have defeated the Demon King and brought peace to the land. But Frieren will long outlive the rest of her former party. How will she come to understand what life means to the people around her? An old enemy returns as Frieren continues her journey north. Decades ago, Frieren and her party defeated a servant of the Demon King called Aura the Guillotine, one of the powerful demons knows as the Seven Sages of Destruction. Now Aura is back with a score to settle. But what price did Frieren pay for victory in the past, and how will the choices she made then affect the present?', 0, 0, '2022-03-08', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (96, 'Frieren: Beyond Journey''s End, Vol. 4 (2022)', 18, 22, 192, '22.84x16.67x2.34 cm', 'Misa “Japanese Ammo”', 'The adventure is over but life goes on for an elf mage just beginning to learn what living is all about. Elf mage Frieren and her courageous fellow adventurers have defeated the Demon King and brought peace to the land. With the great struggle over, they all go their separate ways to live a quiet life. But as an elf, Frieren, nearly immortal, will long outlive the rest of her former party. How will she come to terms with the mortality of her friends? How can she find fulfillment in her own life, and can she learn to understand what life means to the humans around her? Frieren begins a new journey to find the answer. The village priest Sein has no intention of becoming an adventurer, but his desire to find a long-lost friend may lead him to join Frieren’s party on their journey north. They are headed for the magical city of Äußerst, where Frieren can obtain the first-class mage certification needed to enter the Northern Plateau region. At Frieren’s urging, Fern decides to take the certification exam as well, and faces some unexpected competition…', 0, 0, '2022-05-17', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (97, 'Frieren: Beyond Journey''s End, Vol. 5 (2022)', 18, 22, 0, '23.88x16.81x2.27 cm', 'Misa “Japanese Ammo”', 'The adventure is over but life goes on for an elf mage just beginning to learn what living is all about. Elf mage Frieren and her courageous fellow adventurers have defeated the Demon King and brought peace to the land. But Frieren will long outlive the rest of her former party. How will she come to understand what life means to the people around her? Frieren and Fern have ended up on different teams in the highly competitive first-class mage exam. Capturing a rare bird is required to pass, but personality clashes among teammates are making things difficult. Everyone soon realizes there is more at stake than mere success or failure—for some of them, the exam could be a matter of life and death.', 0, 0, '2022-07-19', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (98, 'Frieren: Beyond Journey’s End, Vol. 6 (2022)', 18, 22, 192, '24.44x16.22x1.56 cm', 'Misa “Japanese Ammo”', 'The mages begin the second stage of their certification exam: a dangerous expedition into the innermost depths of the ruins of the king’s tomb. This time, there are no teams and it’s every mage for themselves. Whether they want to team up or not, the challenge they will face—their own clones—will push their skills to the limit. -- VIZ Media', 0, 0, '2022-10-18', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (99, 'Frieren: Beyond Journey''s End, Vol. 7 (2023)', 18, 22, 0, '23.11x16.26x2.16 cm', 'Misa “Japanese Ammo”', 'The adventure is over but life goes on for an elf mage just beginning to learn what living is all about. Elf mage Frieren and her courageous fellow adventurers have defeated the Demon King and brought peace to the land. But Frieren will long outlive the rest of her former party. How will she come to understand what life means to the people around her? Although the examinees have overcome many physical threats, passing the first-class mage exam ultimately comes down to a simple interview with Serie, whose intuition will determine success or failure. Once the exam is over, Frieren, Fern, and Stark set out again on their journey to Aureole, the land where souls rest. While on the road, various encounters provide new challenges—though the most challenging thing Stark and Fern have to face is a date with each other!', 0, 0, '2023-01-17', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (100, 'Yotsuba&!, Vol. 10 (2011)', 19, 24, 226, '24.38x16.34x2.12 cm', 'Amy Forsyth', 'Yotsuba loooooves playing games! When Daddy''s the counter at hide-and-seek, he can never find Yotsuba, ''cause I''m such a good hider! And when we go to the park, Yotsuba''s the bestest at swing-tag-shoe-races. Daddy says Yotsuba''s just making up the rules as we go along, but Yotsuba thinks Daddy''s just a sour loser. But maybe Yotsuba will let him win once in a while...', 0, 0, '2011-12-12', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (101, 'Yotsuba&!, Vol. 11 (2012)', 19, 24, 226, '24.33x13.66x1.94 cm', 'Amy Forsyth', 'Say cheese! Say cheeeese! Yotsuba''s got a shiny new camera! But this new camera is too cool for just Daddy''s silly poses. What else should Yotsuba take? Maybe the nice man at the restaurant who makes udon, or Shaggy Beard at the bike shop. But definitely not that dog down the street. He''s a little scary and . . . Oh no! Watch out, Juralumin!!!! WAHHHH!', 0, 0, '2012-11-20', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (102, 'Yotsuba&!, Vol. 12 (2013)', 19, 24, 226, '23.06x16.04x1.37 cm', 'Amy Forsyth', 'Oops! Your shoe! Your shoe! Your shoe came untied! But don''t worry! Tora taught Yotsuba how to tie a bow like a butterfly! Now Yotsuba can tie Juralumin''s ribbon aaaall by herself. Huh? Why are Yotsuba''s hands blue? Well, see, there was this reeeally pretty blue paint...and Yotsuba thought Daddy would want his stuff to be reeeally pretty blue too... Are Yotsuba''s hands gonna stay blue forever and ever?!!', 0, 0, '2013-11-19', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (103, 'Yotsuba&!, Vol. 13 (2016)', 19, 24, 228, '22.78x16.42x1.50 cm', 'Amy Forsyth', 'Fresh off the excitement of her camping trip, Yotsuba initiates a very productive session of sandbox play in which she instructs Fuuka how to properly run a bakery. But even more exciting is a visit from Grandma! Yotsuba learns how to value and enjoy cleaning, how not to be rude when hoping for souvenirs, and most important, how to cope when Grandma leaves. But don''t worry, she''ll be back someday!', 0, 0, '2016-05-24', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (104, 'Yotsuba&!, Vol. 14 (2018)', 19, 24, 244, '23.95x15.86x1.83 cm', 'Amy Forsyth', 'Everyone''s favorite green-haired five-year-old is back! And this time, Yotsuba''s expanding her horizons by taking a trip to the big city with her dad! The giant amusement park of Tokyo--with all its trains to ride, neighborhoods to explore, and fancy lunches to eat--promises heartwarmingly hilarious adventures in this long-awaited volume!', 0, 0, '2018-11-13', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (105, 'Mark Twain Essays Annotated (2021)', 20, 8, 30, '22.71x15.09x2.18 cm', NULL, 'This is a collection of three of the essays written by Mark Twain. Included is The Fly, Thou Shalt Not Kill, and The War Prayer. The War Prayer is a scathing indictment of war, and particularly of blind patriotic and religious fervor as motivations for war. This was published after his death for fear that his family may be affected by his views.', 0, 0, '2021-01-16', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (106, 'Mark Twain, the Globetrotter: Complete Travel Books, Memoirs & Anecdotes (Illustrated Edition) (2024)', 20, 3, 3262, '23.05x15.75x1.41 cm', NULL, '"Though best known for his adventure novels and humorous stories, Twain was a passionate world traveler and he recorded his journeys in several travel books which were all very popular at the time: ""The Innocents Abroad"" humorously chronicles Twain''s ""Great Pleasure Excursion"" on board the chartered vessel Quaker City through Europe and the Holy Land in 1867. ""Roughing It"" follows the travels of young Mark Twain through the Wild West during the years 1861–1867. The book illustrates many of Twain''s early adventures, including a visit to Salt Lake City, gold and silver prospecting, real-estate speculation and a journey to the Kingdom of Hawaii. ""Old Times on the Mississippi"" is a short account of Twain''s experiences as a cub pilot, learning the Mississippi river. ""A Tramp Abroad"" details Twain''s journey through central and southern Europe with his friend. As the two men make their way through Germany, the Alps, and Italy, they encounter situations made all the more humorous by their reactions to them. ""Life on the Mississippi"" is a memoir by Mark Twain of his days as a steamboat pilot on the Mississippi River before the American Civil War, recounting his trip along the Mississippi River from St. Louis to New Orleans after the War. ""Following the Equator"" – In an attempt to extricate himself from debt, Twain undertook a tour of the British Empire in 1895, a route chosen to provide numerous opportunities for lectures in English. The book is a social commentary, critical of racism towards Blacks, Asians, and Indigenous groups. ""Some Rambling Notes of an Idle Excursion"" presents a series of stories about a trip that Twain and some friends took to Bermuda from New York City. ""Chapters from my Autobiography"" comprises a rambling collection of anecdotes and ruminations of Mark Twain, assembled during his life. Samuel Langhorne Clemens (1835-1910), better known by his pen name Mark Twain, was an American writer, humorist, entrepreneur, publisher, and lecturer."', 0, 0, '2024-01-17', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (107, 'Old Times on the Mississippi (2020)', 20, 12, 105, '23.27x16.86x2.21 cm', NULL, NULL, 0, 0, '2020-09-28', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (108, 'What Is Man? and Other Essays (2019)', 20, 3, 265, '24.88x16.36x2.08 cm', NULL, 'By Mark Twain is a collection of thought-provoking essays that explore the complexities of human nature, society, and existence. Delve into Twain''s witty and insightful commentary on various topics, including human behavior, morality, and the human mind. This book is a must-read for fans of Mark Twain''s literary genius and those interested in philosophical essays.', 0, 0, '2019-11-19', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (109, 'Skip and Loafer Vol. 6 (2022)', 21, 19, 186, '22.48x14.41x1.93 cm', 'Nicole Frasik', 'It''s wintertime in Tokyo! Even as Mitsumi juggles Valentine''s Day infatuations and White Day revelations, the high-school life of this natural-born happiness influencer is hurtling headlong towards spring--and her second year in high school!', 0, 0, '2022-11-29', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (110, 'Skip and Loafer Vol. 7 (2023)', 21, 19, 186, '22.27x13.69x1.71 cm', 'Nicole Frasik', 'Spring has sprung, and Mitsumi and company have finally become second years! The fresh spring breeze carries a lot of firsts for the gang--different classes, encounters with new first-year students, and more. But even when the sailing isn''t quite smooth, they can count on Mitsumi to remind them of what''s important--when you''re a fresh-faced kid in high school, every day feels like a miracle!', 0, 0, '2023-05-09', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (111, 'Skip and Loafer Vol. 8 (2023)', 21, 19, 186, '23.71x15.23x2.19 cm', 'Nicole Frasik', 'They''ve inspired each other as classmates from day one. Now, Mitsumi and Sousuke are ready to cross the line from friendship into true odd coupledom! Mitsumi''s determined to have it all: love, friendship, grades, and student council...but these dizzying days might dazzle the daylights out of her instead!', 0, 0, '2023-10-24', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (112, 'The Apothecary Diaries 01 (Manga) (2020)', 22, 14, 0, '24.19x16.29x2.44 cm', 'Kevin Steinbach', '"After breaking a ""curse"" on the imperial heirs, a palace servant with training in herbal medicine is promoted up the ranks to food taster...and right into the thick of palace intrigue in this lushly illustrated period mystery series! Maomao, a young woman trained in the art of herbal medicine, is forced to work as a lowly servant in the inner palace. Though she yearns for life outside its perfumed halls, she isn''t long for a life of drudgery! Using her wits to break a ""curse"" afflicting the imperial heirs, Maomao attracts the attentions of the handsome eunuch Jinshi and is promoted to attendant food taster. But Jinshi has other plans for the erstwhile apothecary, and soon Maomao is back to brewing potions and...solving mysteries?!"', 0, 0, '2020-12-08', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (113, 'The Apothecary Diaries 02 (Manga) (2021)', 22, 14, 0, '23.24x14.08x2.46 cm', 'Kevin Steinbach', '"After breaking a ""curse"" on the imperial heirs, a palace servant with training in herbal medicine is promoted up the ranks to food taster...and right into the thick of palace intrigue in this lushly illustrated period mystery series! Maomao, a young woman trained in the art of herbal medicine, is forced to work as a lowly servant in the inner palace. Though she yearns for life outside its perfumed halls, she isn''t long for a life of drudgery! Using her wits to break a ""curse"" afflicting the imperial heirs, Maomao attracts the attentions of the handsome eunuch Jinshi and is promoted to attendant food taster. But Jinshi has other plans for the erstwhile apothecary, and soon Maomao is back to brewing potions and...solving mysteries?!"', 0, 0, '2021-02-09', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (114, 'The Apothecary Diaries: Volume 10 (Light Novel) (2024)', 22, 9, 271, '24.68x16.02x1.01 cm', 'Kevin Steinbach', 'Maomao finds herself once more in the western capital. She tries to focus on her work—which isn’t made any easier when she’s paired with not just the quack doctor but the smart-mouthed young physician Tianyu. Meanwhile, Jinshi might be the Imperial younger brother, but the western capital has its own hierarchies, both obvious and hidden. He and the other visitors from the court will have to navigate these halls of power even more carefully than usual, lest they run afoul of enemies they don’t even know they have. Over it all looms the threat of the insect plague, which seems insurmountable—until one man claims to know how to counter the menace. His method is intimately connected to secrets buried deep in the region’s past.', 0, 0, '2024-01-18', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (115, 'The Apothecary Diaries: Volume 7 (Light Novel) (2023)', 22, 9, 243, '22.94x15.65x2.20 cm', 'Kevin Steinbach', 'Maomao attempts the court service exam once more, winning herself a position as an assistant in one of the medical offices. That’s only the beginning of her troubles, though, as a new consort enters the rear palace and brings new riddles with her. What does a former emissary to the court want with three young medical assistants? How is that connected to a mysterious shrine maiden from Shaoh? And will one particular monocled freak ever just mind his own business?', 0, 0, '2023-03-01', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (116, 'The Apothecary Diaries: Volume 8 (Light Novel) (2023)', 22, 9, 253, '22.70x15.28x1.47 cm', 'Kevin Steinbach', 'The whole capital seems to have gone Go-crazy when Lakan publishes a book about his games. He’s got even bigger plans, holding a Go tournament that’s open to all—and rumor has it that if anyone can topple the strategist himself, he’ll grant them any single request they might ask. Jinshi sees an opportunity. But it’s not all fun and games, as Jinshi learns when reports arrive of crops devastated by a plague of locusts in a remote village. All his attempts to forestall the insects’ devastation will soon be put to the test, and the outcome may mean survival or starvation.', 0, 0, '2023-05-29', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (117, 'The Apothecary Diaries: Volume 9 (Light Novel) (2023)', 22, 9, 290, '23.53x13.04x1.30 cm', 'Kevin Steinbach', 'Maomao has effectively become Jinshi’s personal physician, but she’s just a simple apothecary. If she’s going to give him proper medical treatment, she’ll need more than her meager surgical skills. When she turns to her father for help, he says he’ll only teach her if she can pass a test he sets for her. Even if she succeeds at his mysterious request, however, the truth behind the practice of surgery at court may be more than she cares to know. And only once she has the knowledge she needs will she be able to accompany Jinshi on what could be his most dangerous journey yet.', 0, 0, '2023-10-09', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (118, 'Night Sky with Exit Wounds (2017)', 23, 16, 96, '22.19x14.99x2.10 cm', NULL, 'Winner of the 2017 T. S. Eliot Prize ‘Reading Vuong is like watching a fish move: he manages the varied currents of English with muscled intuition.’ New Yorker An extraordinary debut from a young Vietnamese American, Night Sky with Exit Wounds is a book of poetry unlike any other. Steeped in war and cultural upheaval and wielding a fresh new language, Vuong writes about the most profound subjects – love and loss, conflict, grief, memory and desire – and attends to them all with lines that feel newly-minted, graceful in their cadences, passionate and hungry in their tender, close attention: ‘...the chief of police/facedown in a pool of Coca-Cola./A palm-sized photo of his father soaking/beside his left ear.’ This is an unusual, important book: both gentle and visceral, vulnerable and assured, and its blend of humanity and power make it one of the best first collections of poetry to come out of America in years. ‘These are poems of exquisite beauty, unashamed of romance, and undaunted by looking directly into the horrors of war, the silences of history. One of the most important debut collections for a generation.’ Andrew McMillan Winner of the 2017 Felix Dennis Prize for Best First Collection A Guardian / Daily Telegraph Book of the Year PBS Summer Recommendation', 0, 0, '2017-04-04', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (119, 'On Earth We''re Briefly Gorgeous - A Novel (2019)', 23, 16, 258, '22.36x15.62x1.66 cm', NULL, 'The instant New York Times Bestseller • Nominated for the 2019 National Book Award for Fiction “A lyrical work of self-discovery that’s shockingly intimate and insistently universal…Not so much briefly gorgeous as permanently stunning.” —Ron Charles, The Washington Post Ocean Vuong’s debut novel is a shattering portrait of a family, a first love, and the redemptive power of storytelling On Earth We’re Briefly Gorgeous is a letter from a son to a mother who cannot read. Written when the speaker, Little Dog, is in his late twenties, the letter unearths a family’s history that began before he was born — a history whose epicenter is rooted in Vietnam — and serves as a doorway into parts of his life his mother has never known, all of it leading to an unforgettable revelation. At once a witness to the fraught yet undeniable love between a single mother and her son, it is also a brutally honest exploration of race, class, and masculinity. Asking questions central to our American moment, immersed as we are in addiction, violence, and trauma, but undergirded by compassion and tenderness, On Earth We’re Briefly Gorgeous is as much about the power of telling one’s own story as it is about the obliterating silence of not being heard. With stunning urgency and grace, Ocean Vuong writes of people caught between disparate worlds, and asks how we heal and rescue one another without forsaking who we are. The question of how to survive, and how to make of it a kind of joy, powers the most important debut novel of many years. Named a Best Book of the Year by: GQ, Kirkus Reviews, Booklist, Library Journal, TIME, Esquire, The Washington Post, Apple, Good Housekeeping, The New Yorker, The New York Public Library, Elle.com, The Guardian, The A.V. Club, NPR, Lithub, Entertainment Weekly, Vogue.com, The San Francisco Chronicle, Mother Jones, Vanity Fair, The Wall Street Journal Magazine and more!', 0, 0, '2019-06-04', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (120, 'On Earth We''re Briefly Gorgeous - A Novel (2021)', 23, 16, 258, '22.13x16.79x1.76 cm', NULL, 'The instant New York Times Bestseller • Nominated for the 2019 National Book Award for Fiction “A lyrical work of self-discovery that’s shockingly intimate and insistently universal…Not so much briefly gorgeous as permanently stunning.” —Ron Charles, The Washington Post Ocean Vuong’s debut novel is a shattering portrait of a family, a first love, and the redemptive power of storytelling On Earth We’re Briefly Gorgeous is a letter from a son to a mother who cannot read. Written when the speaker, Little Dog, is in his late twenties, the letter unearths a family’s history that began before he was born — a history whose epicenter is rooted in Vietnam — and serves as a doorway into parts of his life his mother has never known, all of it leading to an unforgettable revelation. At once a witness to the fraught yet undeniable love between a single mother and her son, it is also a brutally honest exploration of race, class, and masculinity. Asking questions central to our American moment, immersed as we are in addiction, violence, and trauma, but undergirded by compassion and tenderness, On Earth We’re Briefly Gorgeous is as much about the power of telling one’s own story as it is about the obliterating silence of not being heard. With stunning urgency and grace, Ocean Vuong writes of people caught between disparate worlds, and asks how we heal and rescue one another without forsaking who we are. The question of how to survive, and how to make of it a kind of joy, powers the most important debut novel of many years. Named a Best Book of the Year by: GQ, Kirkus Reviews, Booklist, Library Journal, TIME, Esquire, The Washington Post, Apple, Good Housekeeping, The New Yorker, The New York Public Library, Elle.com, The Guardian, The A.V. Club, NPR, Lithub, Entertainment Weekly, Vogue.com, The San Francisco Chronicle, Mother Jones, Vanity Fair, The Wall Street Journal Magazine and more!', 0, 0, '2021-06-01', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (121, 'Time Is a Mother (2022)', 23, 16, 129, '23.69x14.66x1.03 cm', NULL, '"The New York Times-bestselling collection of poems from the award-winning writer Ocean Vuong ""Take your time with these poems, and return to them often.” —The Washington Post How else do we return to ourselves but to fold The page so it points to the good part In this deeply intimate second poetry collection, Ocean Vuong searches for life among the aftershocks of his mother’s death, embodying the paradox of sitting within grief while being determined to survive beyond it. Shifting through memory, and in concert with the themes of his novel On Earth We’re Briefly Gorgeous, Vuong contends with personal loss, the meaning of family, and the cost of being the product of an American war in America. At once vivid, brave, and propulsive, Vuong’s poems circle fragmented lives to find both restoration as well as the epicenter of the break. The author of the critically acclaimed poetry collection Night Sky With Exit Wounds, winner of the 2016 Whiting Award, the 2017 T. S. Eliot Prize, and a 2019 MacArthur fellow, Vuong writes directly to our humanity without losing sight of the current moment. These poems represent a more innovative and daring experimentation with language and form, illuminating how the themes we perennially live in and question are truly inexhaustible. Bold and prescient, and a testament to tenderness in the face of violence, Time Is a Mother is a return and a forging forth all at once."', 0, 0, '2022-04-05', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (122, 'Delicious in Dungeon (2017)', 24, 24, 192, '24.25x14.38x1.32 cm', 'Taylor Engel', 'With the possibility of starvation as a constant companion, Laios''s party continues advancing deeper into the dungeon. The adventurers have made it to the third floor, but only rotting zombies, ghastly spirits, living paintings, and golems await them--all of which are absolutely inedible. The party has come this far by adapting and learning how to live off the dubious bounties of the dungeon. But how will they manage when even the meanest monster won''t make a decent meal?', 0, 0, '2017-08-22', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (123, 'Delicious in Dungeon World Guide: The Adventurer''s Bible (2022)', 24, 24, 180, '24.38x16.90x1.26 cm', 'Taylor Engel', 'Prepare for adventure! Delve into the depths of Delicious in Dungeon with a smorgasbord of illustrations, secret tales that couldn’t be told before, and detailed information about all the characters! Whether it’s their age, BMI, or the first time they died, this guide has everything there is to know. Get the scoop on all the various races and dungeons found throughout the world. There’s even an encyclopedia of monsters!', 0, 0, '2022-08-23', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (124, 'Delicious in Dungeon, Vol. 1 (2017)', 24, 24, 194, '22.39x15.62x2.03 cm', 'Taylor Engel', '"When young adventurer Laios and his company are attacked and soundly thrashed by a dragon deep in a dungeon, the party loses all its money and provisions...and a member! They''re eager to go back and save her, but there is just one problem: If they set out with no food or coin to speak of, they''re sure to starve on the way! But Laios comes up with a brilliant idea: ""Let''s eat the monsters!"" Slimes, basilisks, and even dragons...none are safe from the appetites of these dungeon-crawling gourmands!"', 0, 0, '2017-05-23', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (125, 'Delicious in Dungeon, Vol. 3 (2017)', 24, 24, 194, '22.87x15.58x1.16 cm', 'Taylor Engel', 'New dangers and new recipes await the party in the third installment of the utterly unique and endearing Delicious in Dungeon!', 0, 0, '2017-11-14', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (126, 'Delicious in Dungeon, Vol. 4 (2018)', 24, 24, 194, '22.13x13.98x1.98 cm', 'Taylor Engel', 'IT''S EAT OR BE EATEN...LITERALLY! The adventurously eating adventurers have finally reached the abandoned orc village where the red dragon was last spotted--and, from the chard and crumbling evidence, where it is still active and on the prowl! No matter the risks, Laios is determined to recover what''s left of Falin and resurrect her, but taking down the massive red dragon will be no easy task, even on a full stomach! Someone''s going to wind up on the menu, but will it be red dragon steak or flame-grilled adventurer?!', 0, 0, '2018-02-27', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (127, 'Delicious in Dungeon, Vol. 5 (2018)', 24, 24, 202, '23.87x13.28x1.47 cm', 'Taylor Engel', 'The party has defeated the Red Dragon and rescued Farin! But their quest won''t be finished until they make it back out of the dungeon unscathed. The adventure''s not complete until everyone returns alive...and full!', 0, 0, '2018-05-22', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (128, 'Delicious in Dungeon, Vol. 6 (2018)', 24, 24, 210, '23.44x16.27x1.18 cm', 'Taylor Engel', 'Old companions reunited in the dungeon...Will ex-comrads be able to work together in the rescue of Falin from the Lunatic Magician? Will a shared meal manage to right past wrongs?', 0, 0, '2018-11-13', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (129, 'Delicious in Dungeon, Vol. 7 (2019)', 24, 24, 220, '23.88x16.19x2.25 cm', 'Taylor Engel', 'It''s eat or be eaten...literally! Massive changes in the dungeon have drawn out new monsters for Laios and his party to take on-and taste! But the shift has attracted the attention of the western elves, whose intervention could spell further trouble not only for Laios, but also for everyone on the island! Facing the threat of the Lunatic Magician from within and the elves from without, the adventurers must rally as a team like never before, especially when forced to confront a beast straight out of Senshi''s nightmares!', 0, 0, '2019-11-12', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (130, 'Delicious in Dungeon, Vol. 8 (2020)', 24, 24, 204, '22.86x13.57x2.19 cm', 'Taylor Engel', 'It''s eat or be eaten...literally! In the dungeon, one false step can be the difference between life and death...or elf and dwarf?! The changeling mushrooms have done their work, leaving Laios and company radically altered, transformed from one race to another! The journey into the heart of the dungeon was never going to be easy, but with new senses, strengths, and statures to contend with, the mission seems more daunting than ever. Thankfully their sense of taste doesn''t seem to have been affected!!', 0, 0, '2020-03-17', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (131, 'Delicious in Dungeon, Vol. 9 (2021)', 24, 24, 210, '22.67x13.99x2.49 cm', 'Taylor Engel', 'It’s eat or be eaten...literally! Laios nears the bottom of the dungeon and his inevitable confrontation with Sissel, the lunatic lord of its labyrinthine halls. In a succubus-induced slumber, Laios comes face-to-face with the winged lion, the source of Sissel’s power. Laios knows only that the lion is held captive by Sissel in the dungeon’s depths, but it could be the Canaries know a great deal more…With their centuries of experience, the elves’ knowledge could shed some light on the truth of the dungeon and its master—or prove to be Laios’s undoing…!', 0, 0, '2021-01-19', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (132, 'Let’s Go For a Walk Outside (Super Simple Storybooks) (2023)', 25, 18, 28, '23.05x16.07x1.88 cm', NULL, 'Go for a joyful walk with the Rhymington Square monsters in this storybook based on the hit YouTube channel Super Simple’s popular music video “Let’s Go For a Walk Outside.” Includes a scavenger hunt checklist! Let’s go for a walk outside and see what we can see! Let’s go for a walk outside, underneath the trees! Stroll through the neighborhood with the Rhymington Square monsters in this charming storybook based on Super Simple’s catchy music video “Let’s Go For a Walk Outside.” Along the way, see how many things from the list you can spot! Can you find the unicorn? What about the cat-shaped cloud? Includes a scavenger hunt list for you to take along when you go for your own walk around the block! Super Simple has more than 30 million subscribers on YouTube. Now families can enjoy their favorite characters, songs, and stories from the screen with Super Simple board books, storybooks, and activity books!', 0, 0, '2023-01-03', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (133, 'Level Up 2023: An AFK Book (2022)', 25, 18, 227, '22.33x15.46x1.68 cm', NULL, 'Check out the ultimate annual video game guide from Scholastic AFK! Level Up 2023 is full of the latest information on the hottest games of the last year, how they were developed, and how to beat them. Get ready for another awesome year of gaming with this ultimate guide to all your favorite games, including a definitive list of the biggest games of the past year and hottest new ones coming in 2023! Level Up 2023 is the most comprehensive guide to all the best games, tech, and streamers, featuring a bunch of the year''s greatest gaming moments.This complete guide is packed with secrets, stats, tips, and tricks for all your favorite games. All games featured in AFK''s Level Up 2023 are rated T for Teen or younger -- perfect for young gamers.', 0, 0, '2022-09-20', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (134, 'My Daddy (Peppa Pig) (2018)', 25, 18, 18, '22.06x16.54x2.29 cm', NULL, 'Learn everything Peppa Pig loves about Daddy Pig in this adorable story based on the hit Nick Jr. TV show! From cuddling up for bedtime stories to playing in the sand at the beach, Peppa and George love spending time with Daddy Pig no matter where they are! Based on the hit Nick Jr. TV show!', 0, 0, '2018-04-24', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (135, 'The Complete Hard Case Crime Stephen King Collection (2021)', 26, 14, 0, '22.85x13.91x1.89 cm', NULL, 'The Complete Hard Case Crime Stephen King Collection, featuring the bestselling titles The Colorado Kid, Joyland, and his newest novel, Later, plus exclusive art cards. Collecting Stephen King''s three homages to the classic crime pulp paperbacks, published by Hard Case Crime. This includes The Colorado Kid (2005), Joyland (2013) and Later (2021). It will also feature three exclusive art cards with alternate cover artwork for the three novels. Set in a small-town North Carolina amusement park in 1973, Joyland tells the story of the summer in which college student Devin Jones comes to work in a fairground and confronts the legacy of a vicious murder, the fate of a dying child, and the ways both will change his life forever. A rookie newspaperwoman learns the true meaning of mystery when she investigates a 25-year-old unsolved and very strange case involving a dead man found on an island off the coast of Maine. The son of a struggling single mother, Jamie Conklin just wants an ordinary childhood. But Jamie is no ordinary child. Born with an unnatural ability, Jamie can see things no one else can. But the cost of using this ability is higher than Jamie can imagine - as he discovers when an NYPD detective draws him into the pursuit of a killer who has threatened to strike from beyond the grave.', 0, 0, '2021-09-01', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (136, 'The Eyes of the Dragon - A Novel (2022)', 26, 20, 400, '24.46x15.82x1.11 cm', NULL, 'A kingdom is in turmoil after old King Roland dies and his worthy successor, Prince Peter, is imprisoned by the evil Flagg and his pawn, young Prince Thomas. But Flagg''s evil plot is not perfect, for he knows naught of Thomas'' terrible secret-- or Prince Peter''s daring plan to escape to claim what is rightfully his.', 0, 0, '2022-05-24', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (137, 'The Institute - A Novel (2019)', 26, 20, 672, '24.12x14.39x1.52 cm', NULL, 'From #1 New York Times bestselling author Stephen King whose “storytelling transcends genre” (Newsday) comes “another winner: creepy and touching and horrifyingly believable” (The Boston Globe) about a group of kids confronting evil. In the middle of the night, in a house on a quiet street in suburban Minneapolis, intruders silently murder Luke Ellis’s parents and load him into a black SUV. The operation takes less than two minutes. Luke will wake up at The Institute, in a room that looks just like his own, except there’s no window. And outside his door are other doors, behind which are other kids with special talents—telekinesis and telepathy—who got to this place the same way Luke did: Kalisha, Nick, George, Iris, and ten-year-old Avery Dixon. They are all in Front Half. Others, Luke learns, graduated to Back Half, “like the roach motel,” Kalisha says. “You check in, but you don’t check out.” In this most sinister of institutions, the director, Mrs. Sigsby, and her staff are ruthlessly dedicated to extracting from these children the force of their extranormal gifts. There are no scruples here. If you go along, you get tokens for the vending machines. If you don’t, punishment is brutal. As each new victim disappears to Back Half, Luke becomes more and more desperate to get out and get help. But no one has ever escaped from the Institute. As psychically terrifying as Firestarter, and with the spectacular kid power of It, The Institute is “first-rate entertainment that has something important to say. We all need to listen” (The Washington Post).', 0, 0, '2019-09-10', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (138, 'The Institute - A Novel (2021)', 26, 20, 672, '23.78x14.03x1.84 cm', NULL, '"In the middle of the night, in a house on a quiet street in suburban Minneapolis, intruders silently murder Luke Ellis'' parents and load him into a black SUV. The operation takes less than two minutes. Luke will wake up at The Institute, in a room that looks just like his own, except there''s no window. And outside his door are other doors, behind which are other kids with special talents--telekinesis and telepathy--who got to this place the same way Luke did: Kalisha, Nick, George, Iris, and 10-year-old Avery Dixon. They are all in Front Half. Others, Luke learns, graduated to Back Half, ""like the roach motel,"" Kalisha says. ""You check in, but you don''t check out."" In this most sinister of institutions, the director, Mrs. Sigsby, and her staff are ruthlessly dedicated to extracting from these children the force of their extranormal gifts. There are no scruples here. If you go along, you get tokens for the vending machines. If you don''t, punishment is brutal. As each new victim disappears to Back Half, Luke becomes more and more desperate to get out and get help. But no one has ever escaped from The Institute."', 0, 0, '2021-06-29', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (139, 'The Man in Black (2019)', 26, 20, 132, '22.86x13.39x1.19 cm', NULL, 'Enter once more the world of Roland Deschain—and the world of the Dark Tower…presented in a stunning graphic novel form that will unlock the doorways to terrifying secrets and bold storytelling as part of the dark fantasy masterwork and magnum opus from #1 New York Times bestselling author Stephen King. “The man in black fled across the desert, and the gunslinger followed.” With these unforgettable words, millions of readers were introduced to Stephen King’s iconic character Roland Deschain of Gilead. Roland is the last of his kind, a “gunslinger” charged with protecting whatever goodness and light remains in his world—a world that “moved on,” as they say. In this desolate reality—a dangerous land filled with ancient technology and deadly magic, and yet one that mirrors our own in frightening ways—Roland is on a spellbinding and soul-shattering quest to locate and somehow save the mystical nexus of all worlds, all universes: the Dark Tower. Now, in the graphic novel series adaptation Stephen King’s The Dark Tower: The Gunslinger, originally published by Marvel Comics in single-issue form and creatively overseen by Stephen King himself, the full story of Roland’s troubled past and ongoing saga is revealed. Sumptuously drawn by Richard Isanove, Sean Phillips, Luke Ross, and Michael Lark, plotted by longtime Stephen King expert Robin Furth, and scripted by New York Times bestselling author Peter David, The Gunslinger adaptation is an extraordinary and terrifying journey—ultimately serving as the perfect introduction for new readers to Stephen King’s modern literary classic The Dark Tower, while giving longtime fans thrilling adventures transformed from his blockbuster novels. Roland closes in at last on his elusive nemesis, the Man in Black. At Roland’s side is his newest ka-tet-mate, Jake Chambers—a boy from some strange other Earth of subways, turnpikes, and fluorescent lights. But to reach the Man in Black—and save the Dark Tower—Roland is prepared to risk it all. His life…his very soul…and even Jake on this final leg of a treacherous journey, to the destiny Roland has pursued for many long years….', 0, 0, '2019-06-04', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (140, 'The Way Station (2019)', 26, 20, 132, '23.63x13.09x1.98 cm', NULL, 'Enter once more the world of Roland Deschain—and the world of the Dark Tower—presented in a stunning graphic novel form unlocking the doorways to terrifying secrets and bold storytelling as part of the dark fantasy masterwork and magnum opus from #1 New York Times bestselling author Stephen King. “The man in black fled across the desert, and the gunslinger followed.” With these unforgettable words, millions of readers were introduced to Stephen King’s iconic character Roland Deschain of Gilead. Roland is the last of his kind, a “gunslinger” charged with protecting whatever goodness and light remains in his world—a world that “moved on,” as they say. In this desolate reality—a dangerous land filled with ancient technology and deadly magic, and yet one that mirrors our own in frightening ways—Roland is on a spellbinding and soul-shattering quest to locate and somehow save the mystical nexus of all worlds, all universes: the Dark Tower. Now, in the graphic novel series adaptation Stephen King''s The Dark Tower: The Gunslinger, originally published by Marvel Comics in single-issue form and creatively overseen by Stephen King himself, the full story of Roland’s troubled past and ongoing saga is revealed. Sumptuously drawn by Richard Isanove, Sean Phillips, Luke Ross, Michael Lark, and Laurence Campbell, plotted by longtime Stephen King expert Robin Furth, and scripted by New York Times bestselling author Peter David, The Gunslinger adaptation is an extraordinary and terrifying journey—ultimately serving as the perfect introduction for new readers to Stephen King’s modern literary classic The Dark Tower, while giving longtime fans thrilling adventures transformed from his blockbuster novels. Roland has barely escaped the frightening and deadly trap set for him by the elusive Man in Black in the sleepy little town of Tull—a place centered in the apotheosis of all deserts, and where the sinister sorcerer’s power had taken hold. Leaving death and despair in his wake, Roland soon arrives at a mysterious way station…and meets a young boy, Jake Chambers, who has seemingly appeared out of nowhere from another place and time. But who is this sophisticated child, and what is his connection to Roland’s quest and the Man in Black himself?', 0, 0, '2019-05-07', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (141, 'Chainsaw Man Box Set - Includes volumes 1-11 (2023)', 27, 22, 0, '24.06x14.28x1.69 cm', 'Amanda Haley', 'Broke young man + chainsaw demon = Chainsaw Man! Denji was a small-time devil hunter just trying to survive in a harsh world. After being killed on a job, he is revived by his pet devil Pochita and becomes something new and dangerous—Chainsaw Man! The rip-roaring first arc of Chainsaw Man, all in one jaw-dropping box set! This box set contains the first 11 volumes of the global hit Chainsaw Man as well as an exclusive double-sided full-color poster.', 0, 0, '2023-09-26', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (142, 'Chainsaw Man, Vol. 6 (2021)', 27, 22, 192, '22.12x15.13x2.48 cm', 'Amanda Haley', 'Broke young man + chainsaw dog demon = Chainsaw Man! Denji was a small-time devil hunter just trying to survive a harsh world. After being killed on a job, Denji is revived by his pet devil-dog Pochita and becomes something new and dangerous—Chainsaw Man! A new girl named Reze has shown up and swept innocent Denji right off his feet. But is Reze exactly what she seems? (Spoiler: Nope!) Is Denji about to fi nd happiness once and for all? (Spoiler: Nope!) Prepare for the storm of all storms when Chainsaw Man looks for true love!', 0, 0, '2021-08-03', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (143, 'Chainsaw Man, Vol. 7 (2021)', 27, 22, 200, '23.71x14.08x2.15 cm', 'Amanda Haley', 'Broke young man + chainsaw dog demon = Chainsaw Man! Denji was a small-time devil hunter just trying to survive a harsh world. After being killed on a job, Denji is revived by his pet devil-dog Pochita and becomes something new and dangerous—Chainsaw Man! Denji''s career as a devil hunter is really taking off! And after he appears on TV, the whole world knows about the mysterious Chainsaw Man. But that’s not all good news, considering assassins are now on their way to Japan to claim his heart! Can Denji’s team keep him safe from the world’s most dangerous killers?', 0, 0, '2021-10-05', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (144, 'Chainsaw Man, Vol. 8 (2021)', 27, 22, 192, '24.46x13.33x2.43 cm', 'Amanda Haley', 'Broke young man + chainsaw dog demon = Chainsaw Man! Denji was a small-time devil hunter just trying to survive a harsh world. After being killed on a job, Denji is revived by his pet devil-dog Pochita and becomes something new and dangerous—Chainsaw Man! As Quanxi and the members of the Special Division battle it out over Denji, the mysterious Santa Claus makes his move. But things are not as they appear, and nobody will be prepared for the darkness and despair about to be unleashed!', 0, 0, '2021-12-07', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (145, 'Chainsaw Man, Vol. 9 (2022)', 27, 22, 192, '23.87x14.68x1.26 cm', 'Amanda Haley', 'Broke young man + chainsaw dog demon = Chainsaw Man! Denji was a small-time devil hunter just trying to survive a harsh world. After being killed on a job, Denji is revived by his pet devil-dog Pochita and becomes something new and dangerous—Chainsaw Man! With the battle against the Gun Devil fast approaching, Aki seems to be having a change of heart. Why is he suddenly willing to give up the revenge he’s worked so hard for? And when the Gun Devil finally appears, Denji and allies will face a nightmare of unimaginable pain.', 0, 0, '2022-02-01', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (146, 'Fire Punch, Vol. 5 (2019)', 27, 22, 210, '22.45x15.34x2.14 cm', 'Amanda Haley', 'After painful words are unleashed and Togata’s secret is finally exposed, the revelations leave Agni shaken. Knowing he must face his destiny, Agni seeks out the man at the root of all of his suffering! -- VIZ Media', 0, 0, '2019-01-15', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (147, 'Fire Punch, Vol. 6 (2019)', 27, 22, 210, '23.11x16.21x1.78 cm', 'Amanda Haley', 'In order to absorb all life, Judah has been turned into a massive tree by the Ice Witch. And just as Agni loses his will to live, Judah’s sorrowful pleas for death echo in his ear. What will await the world after its destruction and rebirth is complete? -- VIZ Media', 0, 0, '2019-04-16', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (148, 'Fire Punch, Vol. 7 (2019)', 27, 22, 208, '22.96x16.45x2.02 cm', 'Amanda Haley', 'With his promise to Tena imprinted on his mind, Agni can only enjoy an inauthentic peace coated thickly with lies. His existence blames, tortures and undermines him, becoming an affliction worse than the anger, pain and madness he’d experienced as Fire Punch. As Agni searches desperately for an escape from this hell, an old friend suddenly appears. -- VIZ Media', 0, 0, '2019-07-16', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (149, 'Fire Punch, Vol. 8 (2019)', 27, 22, 211, '24.71x14.48x2.37 cm', 'Amanda Haley', 'Agni’s body is once again engulfed in flames, and he burns everyone and everything in his path on his way to Judah. Sun’s blind belief explodes into fury as his savior Agni becomes the villain! Will it be an inferno of revenge or a redeeming light finally end this frozen world?! -- VIZ Media', 0, 0, '2019-10-15', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (150, 'Tatsuki Fujimoto Before Chainsaw Man: 17-21 (2023)', 27, 22, 169, '24.76x13.91x2.30 cm', 'Amanda Haley', 'Alien invasions, high school romances, and even bloody vampire action—all this and more awaits in four compelling short stories that reveal the starting point of Tatsuki Fujimoto, the twisted mastermind behind Chainsaw Man. -- VIZ Media', 0, 0, '2023-01-17', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (151, 'Tatsuki Fujimoto Before Chainsaw Man: 22-26 (2023)', 27, 22, 0, '22.42x14.94x1.24 cm', 'Amanda Haley', 'See the origins of Chainsaw Man mastermind Tatsuki Fujimoto! See the origins of the mad genius who created Chainsaw Man! This short story manga collection features Tatsuki Fujimoto’s earliest work. It’s rough, it’s raw, and it’s pure Tatsuki Fujimoto! Killer mermaids, gender swapping, and a devilish little sister with the power to end the world—four more unforgettable short stories that reveal the starting point of Tatsuki Fujimoto, the twisted mastermind behind Chainsaw Man.', 0, 0, '2023-04-18', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (152, 'Blue Period 4 (2021)', 28, 11, 210, '23.28x14.10x1.29 cm', 'Ajani A. Oloye ', 'Yatora now has new materials in his tool box and a wider range of expression under his belt. But a week before the first exam, Ooba-sensei says he’s missing a crucial edge… With so much at stake, Yatora’s self-doubt brings him lower than ever before. Still, he has his fire, his resilience—and he might just get a lucky break, too.', 0, 0, '2021-08-17', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (153, 'Blue Period 5 (2021)', 28, 11, 208, '24.47x15.26x2.31 cm', 'Ajani A. Oloye ', 'SELF-PORTRAIT Yatora makes the best of a bad situation during TUA''s first exam, and he must surpass these efforts for the second. But after all he’s gone through, Yatora is feeling a little out of sorts. To get back on track, he’ll have to step out of the studio and into new lighting… With the help of an old friend, Yatora bares his soul and some skin to take on his latest challenge: the nude self-portrait.', 0, 0, '2021-11-30', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (154, 'Blue Period 7 (2022)', 28, 14, 0, '23.97x16.01x1.52 cm', 'Ajani A. Oloye ', 'Winner of the 2020 Manga Taisho Grand Prize! A manga about the struggles and rewards of a life dedicated to art. Popular guy Yatora realizes he''s just going through the motions to make other people happy and finds himself in a new passion: painting. But untethering yourself from all your past expectations is dangerous as well as thrilling... FIRST TRY Tokyo University of the Arts is now Yatora’s campus, where each new encounter with colleagues, professors, and assignments challenges everything he’s ever known! Self-doubt and life’s hardships still lurk around every corner, but running into an old friend may just turn things around.', 0, 0, '2022-06-28', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (155, 'Blue Period 8 (2022)', 28, 11, 208, '24.94x14.44x1.03 cm', 'Ajani A. Oloye ', 'CITYSCAPE After a disappointing first assignment, Yatora is encouraged by Maki and is determined to move forward. His next assignment is a scenery of Tokyo. As he takes on new materials and methods, he also becomes exposed to new ways of seeing the city he thought he knew so well. Can Yatora finally evolve beyond the “exam art” that got him into art school…?', 0, 0, '2022-07-26', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (156, 'Is This a Dagger Which I See Before Me? (2016)', 29, 16, 128, '24.35x16.22x1.51 cm', NULL, '''And when I shall die, Take him and cut him out in little stars.'' This collection of Shakespeare''s soliloquies, including both old favourites and lesser-known pieces, shows him at his dazzling best. One of 46 new books in the bestselling Little Black Classics series, to celebrate the first ever Penguin Classic in 1946. Each book gives readers a taste of the Classics'' huge range and diversity, with works from around the world and across the centuries - including fables, decadence, heartbreak, tall tales, satire, ghosts, battles and elephants.', 0, 0, '2016-03-03', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (157, 'King John - Third Series (2018)', 29, 1, 376, '24.63x13.84x2.26 cm', NULL, 'The Arden Shakespeare is the established scholarly edition of Shakespeare''s plays. Now in its third series, Arden offers the best in contemporary scholarship. Each volume guides you to a deeper understanding and appreciation of Shakespeare''s plays. This edition of King John provides: - A clear and authoritative text, edited to the highest standards of scholarship. - Detailed notes and commentary on the same page as the text. - A full, illustrated introduction to the play''s historical, cultural and performance contexts. - A full index to the introduction and notes. - A select bibliography of references and further reading. With a wealth of helpful and incisive commentary, The Arden Shakespeare is the finest edition of Shakespeare you can find. King John tells the story of John''s struggle to retain the crown in the face of alternative claims to the throne from France and is one of the earlier history plays. The new Arden Third Series edition offers students a comprehensive introduction exploring the play''s relationship to its source and to later plays in the history cycle, as well as giving a full account of its critical and performance history, including key productions in 2015 which marked the anniversary of Magna Carta. As such this is the most detailed, informative and up-to-date student edition available.', 0, 0, '2018-02-22', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (158, 'King Lear (2015)', 29, 20, 400, '22.32x13.46x1.70 cm', NULL, '"""Presents Shakespeare''s tragedy in which an English king foolishly splits his kingdom between the two daughters plotting his doom and disinherits his favorite for speaking out against him."" --"', 0, 0, '2015-10-20', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (159, 'The New Oxford Shakespeare - The Complete Works (2016)', 29, 15, 3393, '24.88x16.15x2.32 cm', NULL, 'The Complete Works: Modern Critical Edition is part of the landmark New Oxford Shakespeare--an entirely new consideration of all of Shakespeare''s works, edited afresh from all the surviving original versions of his work, and drawing on the latest literary, textual, and theatrical scholarship.This single illustrated volume is expertly edited to frame the surviving original versions of Shakespeare''s plays, poems, and early musical scores around the latest literary, textual, and theatrical scholarship to date.', 0, 0, '2016-02-01', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (160, 'The RSC Shakespeare: the Complete Works (2022)', 29, 1, 2549, '24.75x16.59x1.58 cm', NULL, '"""The text of any Shakespeare play is a living negotiable entity: scholarship and theatre practice work together to keep the plays alive and vividly present."" - Gregory Doran, RSC Artistic Director Developed in partnership with the Royal Shakespeare Company, this Complete Works of William Shakespeare combines exemplary textual scholarship with beautiful design. Curated by expert editors Sir Jonathan Bate and Professor Eric Rasmussen, the text in this collection is based on the iconic 1623 First Folio: the first and original Complete Works lovingly assembled by Shakespeare''s fellow actors, and the version of Shakespeare''s text preferred by many actors and directors today. This stunning revised edition goes further to present Shakespeare''s plays as they were originally intended - as living theatre to be enjoyed and performed on stage. Along with new colour photographs from a vibrant range of RSC productions, a new Stage Notes feature documenting the staging choices in 100 RSC productions showcases the myriad ways in which Shakespeare''s plays can be brought to life. Now featuring the entire range of Shakespeare''s plays, poems and sonnets, this edition is expanded to include both The Passionate Pilgrim and A Lover''s Complaint. Along with Bate''s excellent general introduction and short essays, this collection includes a range of aids to the reader such as on-page notes explaining unfamiliar terms and key facts boxes providing plot summaries and additional helpful context. A Complete Works for the 21st century, this versatile and highly collectable edition will inspire students, theatre practitioners and lovers of Shakespeare everywhere."', 0, 0, '2022-04-21', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (161, 'The Works of William Shakespeare - Vol. V (2023)', 29, 2, 458, '23.53x16.61x1.33 cm', NULL, 'Reprint of the original, first published in 1859. The publishing house Anatiposi publishes historical books as reprints. Due to their age, these books may have missing pages or inferior quality. Our aim is to preserve these books and make them available to the public so that they do not get lost.', 0, 0, '2023-04-29', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (162, 'The Works of William Shakespeare - Vol. V (2024)', 29, 2, 602, '23.02x15.97x1.42 cm', NULL, 'Reprint of the original, first published in 1875.', 0, 0, '2024-03-05', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (163, 'The Works of William Shakespeare - Vol. VI (2024)', 29, 2, 718, '22.28x16.92x1.62 cm', NULL, 'Reprint of the original, first published in 1875.', 0, 0, '2024-03-06', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (164, 'The Works of William Shakespeare - Vol. VII (2024)', 29, 2, 774, '22.09x13.12x1.29 cm', NULL, 'Reprint of the original, first published in 1875.', 0, 0, '2024-03-07', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (165, 'The Works of William Shakespeare. Life, Glossary. Reprinted from the Early Editions and Compared with Recent Commentators - Vol. VI (2024)', 29, 2, 722, '24.62x16.22x2.15 cm', NULL, 'Reprint of the original, first published in 1875.', 0, 0, '2024-03-11', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (166, 'William Shakespeare Tragedies (2020)', 29, 20, 704, '24.21x14.19x1.57 cm', NULL, 'Twelve of Shakespeare’s most profound and moving dramas in one elegant volume. William Shakespeare’s tragedies introduced the world to some of the most well-known characters in literature, including Romeo, Juliet, Macbeth, Hamlet, King Lear, and Othello. This handsome Word Cloud volume includes all twelve works from the First Folio that are commonly classified as tragedies—but the feelings that Shakespeare’s words can evoke range across the spectrum of human emotion.', 0, 0, '2020-04-14', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (167, 'Dandadan, Vol. 1 (2022)', 30, 22, 208, '22.72x14.90x1.32 cm', 'Kumar Sivasubramanian ', 'A nerd must fight powerful spirits and aliens all vying for the secret power of his “family jewel,” so who better to fight alongside him than his high school crush and a spirit granny?! Momo Ayase and Okarun are on opposite sides of the paranormal spectrum regarding what they’ll believe in and what they won’t. Their quest to prove each other wrong leads them down a path of secret crushes and paranormal battles they’ll have to participate in to believe! Momo Ayase strikes up an unusual friendship with her school’s UFO fanatic, whom she nicknames “Okarun” because he has a name that is not to be said aloud. While Momo strongly believes in spirits, she thinks aliens are nothing but nonsense. Her new friend, however, thinks quite the opposite. To settle matters, the two set out to prove each other wrong—Momo to a UFO hotspot and Okarun to a haunted tunnel! What unfolds next is a beautiful story of young love…and oddly horny aliens and spirits?', 0, 0, '2022-10-11', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (168, 'Dandadan, Vol. 10 (2024)', 30, 22, 0, '22.35x16.73x1.64 cm', 'Kumar Sivasubramanian ', 'A nerd must fight powerful spirits and aliens all vying for the secret power of his “family jewel,” so who better to fight alongside him than his high school crush and a spirit granny?! Momo Ayase and Okarun are on opposite sides of the paranormal spectrum regarding what they’ll believe in and what they won’t. Their quest to prove each other wrong leads them down a path of secret crushes and paranormal battles they’ll have to participate in to believe! To avoid being late for school on Vamola’s first day, Momo takes the mysterious kaiju girl on a shortcut through some off-limits ruins. But the spell she performs with Vamola for protection doesn’t work, and she runs into the slit-mouthed woman who resides there! The two narrowly escape, and it’s only later that Momo realizes the real danger they were in. So why is it that for Momo’s own safety, Seiko is now forbidding her from leaving her room after 10 p.m. for the next week?!', 0, 0, '2024-03-14', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (169, 'Dandadan, Vol. 2 (2023)', 30, 22, 0, '23.35x13.13x1.60 cm', 'Kumar Sivasubramanian ', 'A nerd must fight powerful spirits and aliens all vying for the secret power of his “family jewel,” so who better to fight alongside him than his high school crush and a spirit granny?! Momo Ayase and Okarun are on opposite sides of the paranormal spectrum regarding what they’ll believe in and what they won’t. Their quest to prove each other wrong leads them down a path of secret crushes and paranormal battles they’ll have to participate in to believe! Okarun, a high school boy obsessed with supernatural phenomena, is cursed by Turbo Granny! To break the curse, Okarun and his classmate Momo Ayase challenge Turbo Granny to a high-stakes race. But Turbo Granny’s assassin, a bound spirit in the form of a giant crab, has other plans!', 0, 0, '2023-01-10', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (170, 'Dandadan, Vol. 3 (2023)', 30, 22, 0, '22.98x14.57x1.06 cm', 'Kumar Sivasubramanian ', 'A nerd must fight powerful spirits and aliens all vying for the secret power of his “family jewel,” so who better to fight alongside him than his high school crush and a spirit granny?! Momo Ayase and Okarun are on opposite sides of the paranormal spectrum regarding what they’ll believe in and what they won’t. Their quest to prove each other wrong leads them down a path of secret crushes and paranormal battles they’ll have to participate in to believe! Okarun, a high school boy obsessed with supernatural phenomena, has lost his precious family jewels in a battle with Turbo Granny! His classmate Aira ends up finding one, and it awakens spiritual powers within her. Around this same time, a strange woman in a red dress appears at the school and attacks Aira, Okarun, and Momo! Who is she, and what’s with her obsession with Aira?', 0, 0, '2023-04-11', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (171, 'Dandadan, Vol. 4 (2023)', 30, 22, 0, '23.15x14.18x1.29 cm', 'Kumar Sivasubramanian ', 'A nerd must fight powerful spirits and aliens all vying for the secret power of his “family jewel,” so who better to fight alongside him than his high school crush and a spirit granny?! Momo Ayase and Okarun are on opposite sides of the paranormal spectrum regarding what they’ll believe in and what they won’t. Their quest to prove each other wrong leads them down a path of secret crushes and paranormal battles they’ll have to participate in to believe! The banana-stealing Serpoians are back! And they’ve trapped Okarun, Momo, and Aira in a void to have another go at stealing their reproductive organs. The subsequent battle causes the powers of the Acrobatic Silky dwelling within Aira to awaken, and she temporarily joins forces with Momo and Okarun. But will they be any match for their opponents after the aliens merge with their minions to form an even greater being?!', 0, 0, '2023-07-11', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (172, 'Dandadan, Vol. 5 (2023)', 30, 22, 0, '24.54x15.84x2.34 cm', 'Kumar Sivasubramanian ', 'A nerd must fight powerful spirits and aliens all vying for the secret power of his “family jewel,” so who better to fight alongside him than his high school crush and a spirit granny?! Momo Ayase and Okarun are on opposite sides of the paranormal spectrum regarding what they’ll believe in and what they won’t. Their quest to prove each other wrong leads them down a path of secret crushes and paranormal battles they’ll have to participate in to believe! Okarun and the gang are off to a hot springs town to investigate the mystery of Jiji’s family. But when they get there, a strange family in cahoots with the police starts causing trouble for them, and before Okarun and the others can get to the bottom of the mystery, Momo ends up as an offering to the local god!', 0, 0, '2023-10-10', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (173, 'Dandadan, Vol. 6 (2024)', 30, 22, 197, '24.22x15.74x2.14 cm', 'Kumar Sivasubramanian ', 'The Kito clan tosses Okarun and the gang into an alternate-reality version of Jiji’s family home as an offering to the serpent god! Still imprisoned there is a former child sacrifice who goes on to possess the softhearted Jiji, and the two of them become the Evil Eye. When he then attacks Okarun and Momo, Okarun decides to take him on alone and sends Momo back aboveground to get help. There, Momo comes up with an ingenious plan to get rid of the giant serpent god, but can she pull it off on her own in time to save Jiji? -- VIZ Media', 0, 0, '2024-01-09', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (174, 'Dandadan, Vol. 7 (2024)', 30, 22, 0, '23.58x13.04x1.39 cm', 'Kumar Sivasubramanian ', 'A nerd must fight powerful spirits and aliens all vying for the secret power of his “family jewel,” so who better to fight alongside him than his high school crush and a spirit granny?! Momo Ayase and Okarun are on opposite sides of the paranormal spectrum regarding what they’ll believe in and what they won’t. Their quest to prove each other wrong leads them down a path of secret crushes and paranormal battles they’ll have to participate in to believe! Okarun and the others emerge victorious in their fight against the serpent god, but one lingering threat remains—the Evil Eye’s possession of Jiji. Even Grandma Seiko’s exorcism is of no use. To complicate matters further, whenever Jiji comes into contact with cold water, the Evil Eye regains control and attacks! Out of options, the group decides their best course of action is to watch over Jiji together until a proper exorcism can be performed—whenever that may be!', 0, 0, '2024-03-14', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (175, 'Dandadan, Vol. 9 (2024)', 30, 22, 0, '23.73x13.74x2.13 cm', 'Kumar Sivasubramanian ', 'A seemingly unbeatable kaiju is on the attack! Okarun hits on the idea of revisualizing the nanoskin that repaired Momo’s house into a form they can use to counterattack, but he lacks the visual abilities needed, so it’s Kinta Sakata—aka Erosuke—to the rescue! The nerd uses his vast knowledge of sci-fi to transform Momo’s house into...a giant Buddha robot?! -- VIZ Media', 0, 0, '2024-03-10', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (176, 'Cherry Magic! Thirty Years of Virginity Can Make You a Wizard?! 01 (2020)', 31, 14, 0, '24.75x14.44x1.85 cm', NULL, 'It''s complicated: A thirty-year-old virgin gets more than he bargained for when his newfound magical power reveals he''s the object of his male coworker''s affections! Adachi, a thirty-year-old virgin, discovers he has the magical power to read the minds of people he touches. Unfortunately, the ability just makes him miserable since he doesn''t know how to use it well! And to make matters worse, when he accidentally reads the mind of his very competent, handsome colleague, Adachi discovers the guy has a raging crush on none other than Adachi himself! Things are about to get VERY awkward!', 0, 0, '2020-03-10', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (177, 'Cherry Magic! Thirty Years of Virginity Can Make You a Wizard?! 03 (2021)', 31, 14, 0, '24.89x16.01x2.22 cm', NULL, 'It''s complicated: A thirty-year-old virgin gets more than he bargained for when his newfound magical power reveals he''s the object of his male coworker''s affections! Ever since he was blessed (cursed?) on his thirtieth birthday with the magical ability to read the minds of those he touches, virginal office drone Adachi has been put through the ringer, especially after discovering his colleague Kurosawa''s feelings for him! And just when it seems like Adachi might be able to take his friendship with Kurosawa to the next level, Adachi''s complex about not having any romantic experience gets in the way. Unfortunately for Adachi, taking things nice and slow is out the window when it turns out the two of them are going to be sharing an apartment!!', 0, 0, '2021-05-25', 'false
', 0);
INSERT INTO Book ( id, title, authorid, publisherid, pageCount, dimension, translatorName, overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount ) VALUES (178, 'Cherry Magic! Thirty Years of Virginity Can Make You a Wizard?! 04 (2022)', 31, 14, 0, '24.53x16.61x1.10 cm', NULL, 'It''s complicated: A thirty-year-old virgin gets more than he bargained for when his newfound magical power reveals he''s the object of his male coworker''s affections! Gifted (cursed?) at the age of 30 with the ability to read the mind of whomever he touches (even the coworker with the hots for him!), virginal businessman-turned-wizard Adachi is taking his first tentative steps into love! After being wooed within an inch of his life by the tenacious Kurosawa, Adachi is at last on board with his feelings for his handsome, kind colleague and has come around to the idea of Kurosawa as his very first significant other. But now that the pair are officially an item, Kurosawa is chomping at the bit to do all the couple stuff he''s been dreaming about, including dating! There''s just one little problem: Adachi, the 30-year-old virgin, has never even been on a date!', 0, 0, '2022-04-26', 'false', 0);
INSERT INTO Category (id, name, ishidden) VALUES (1, 'Art', 'false');
INSERT INTO Category (id, name, ishidden) VALUES (2, 'Award Winning', 'false');
INSERT INTO Category (id, name, ishidden) VALUES (3, 'Comics & Graphic Novels', 'false');
INSERT INTO Category (id, name, ishidden) VALUES (4, 'Cooking', 'false');
INSERT INTO Category (id, name, ishidden) VALUES (5, 'Cowboys', 'false');
INSERT INTO Category (id, name, ishidden) VALUES (6, 'Drama', 'false');
INSERT INTO Category (id, name, ishidden) VALUES (7, 'Fantasy', 'false');
INSERT INTO Category (id, name, ishidden) VALUES (8, 'Fiction', 'false');
INSERT INTO Category (id, name, ishidden) VALUES (9, 'Horror', 'false');
INSERT INTO Category (id, name, ishidden) VALUES (10, 'Identity', 'false');
INSERT INTO Category (id, name, ishidden) VALUES (11, 'Juvenile Fiction', 'false');
INSERT INTO Category (id, name, ishidden) VALUES (12, 'Juvenile Nonfiction', 'false');
INSERT INTO Category (id, name, ishidden) VALUES (13, 'LGBTQ+', 'false');
INSERT INTO Category (id, name, ishidden) VALUES (14, 'Literary Collections', 'false');
INSERT INTO Category (id, name, ishidden) VALUES (15, 'Literary Criticism', 'false');
INSERT INTO Category (id, name, ishidden) VALUES (16, 'Manga', 'false');
INSERT INTO Category (id, name, ishidden) VALUES (17, 'Medical', 'false');
INSERT INTO Category (id, name, ishidden) VALUES (18, 'Mystery & Detective', 'false');
INSERT INTO Category (id, name, ishidden) VALUES (19, 'Poetry', 'false');
INSERT INTO Category (id, name, ishidden) VALUES (20, 'Robots', 'false');
INSERT INTO Category (id, name, ishidden) VALUES (21, 'Romance', 'false');
INSERT INTO Category (id, name, ishidden) VALUES (22, 'Travel', 'false');
INSERT INTO Category (id, name, ishidden) VALUES (23, 'Young Adult Fiction', 'false');
INSERT INTO BookCategory (bookId, categoryId) VALUES (152, 1);
INSERT INTO BookCategory (bookId, categoryId) VALUES (152, 2);
INSERT INTO BookCategory (bookId, categoryId) VALUES (152, 3);
INSERT INTO BookCategory (bookId, categoryId) VALUES (152, 13);
INSERT INTO BookCategory (bookId, categoryId) VALUES (152, 16);
INSERT INTO BookCategory (bookId, categoryId) VALUES (153, 1);
INSERT INTO BookCategory (bookId, categoryId) VALUES (153, 2);
INSERT INTO BookCategory (bookId, categoryId) VALUES (153, 3);
INSERT INTO BookCategory (bookId, categoryId) VALUES (153, 13);
INSERT INTO BookCategory (bookId, categoryId) VALUES (153, 16);
INSERT INTO BookCategory (bookId, categoryId) VALUES (154, 1);
INSERT INTO BookCategory (bookId, categoryId) VALUES (154, 2);
INSERT INTO BookCategory (bookId, categoryId) VALUES (154, 3);
INSERT INTO BookCategory (bookId, categoryId) VALUES (154, 13);
INSERT INTO BookCategory (bookId, categoryId) VALUES (154, 16);
INSERT INTO BookCategory (bookId, categoryId) VALUES (155, 1);
INSERT INTO BookCategory (bookId, categoryId) VALUES (155, 2);
INSERT INTO BookCategory (bookId, categoryId) VALUES (155, 3);
INSERT INTO BookCategory (bookId, categoryId) VALUES (155, 13);
INSERT INTO BookCategory (bookId, categoryId) VALUES (155, 16);
INSERT INTO BookCategory (bookId, categoryId) VALUES (83, 8);
INSERT INTO BookCategory (bookId, categoryId) VALUES (141, 3);
INSERT INTO BookCategory (bookId, categoryId) VALUES (141, 16);
INSERT INTO BookCategory (bookId, categoryId) VALUES (142, 2);
INSERT INTO BookCategory (bookId, categoryId) VALUES (142, 3);
INSERT INTO BookCategory (bookId, categoryId) VALUES (142, 16);
INSERT INTO BookCategory (bookId, categoryId) VALUES (143, 2);
INSERT INTO BookCategory (bookId, categoryId) VALUES (143, 3);
INSERT INTO BookCategory (bookId, categoryId) VALUES (143, 16);
INSERT INTO BookCategory (bookId, categoryId) VALUES (144, 2);
INSERT INTO BookCategory (bookId, categoryId) VALUES (144, 3);
INSERT INTO BookCategory (bookId, categoryId) VALUES (144, 16);
INSERT INTO BookCategory (bookId, categoryId) VALUES (145, 2);
INSERT INTO BookCategory (bookId, categoryId) VALUES (145, 3);
INSERT INTO BookCategory (bookId, categoryId) VALUES (145, 16);
INSERT INTO BookCategory (bookId, categoryId) VALUES (176, 3);
INSERT INTO BookCategory (bookId, categoryId) VALUES (176, 16);
INSERT INTO BookCategory (bookId, categoryId) VALUES (177, 3);
INSERT INTO BookCategory (bookId, categoryId) VALUES (177, 16);
INSERT INTO BookCategory (bookId, categoryId) VALUES (178, 3);
INSERT INTO BookCategory (bookId, categoryId) VALUES (178, 16);
INSERT INTO BookCategory (bookId, categoryId) VALUES (167, 3);
INSERT INTO BookCategory (bookId, categoryId) VALUES (167, 16);
INSERT INTO BookCategory (bookId, categoryId) VALUES (168, 3);
INSERT INTO BookCategory (bookId, categoryId) VALUES (168, 16);
INSERT INTO BookCategory (bookId, categoryId) VALUES (169, 3);
INSERT INTO BookCategory (bookId, categoryId) VALUES (169, 16);
INSERT INTO BookCategory (bookId, categoryId) VALUES (170, 3);
INSERT INTO BookCategory (bookId, categoryId) VALUES (170, 16);
INSERT INTO BookCategory (bookId, categoryId) VALUES (171, 3);
INSERT INTO BookCategory (bookId, categoryId) VALUES (171, 16);
INSERT INTO BookCategory (bookId, categoryId) VALUES (172, 3);
INSERT INTO BookCategory (bookId, categoryId) VALUES (172, 16);
INSERT INTO BookCategory (bookId, categoryId) VALUES (173, 3);
INSERT INTO BookCategory (bookId, categoryId) VALUES (173, 16);
INSERT INTO BookCategory (bookId, categoryId) VALUES (174, 3);
INSERT INTO BookCategory (bookId, categoryId) VALUES (174, 16);
INSERT INTO BookCategory (bookId, categoryId) VALUES (175, 3);
INSERT INTO BookCategory (bookId, categoryId) VALUES (175, 16);
INSERT INTO BookCategory (bookId, categoryId) VALUES (30, 8);
INSERT INTO BookCategory (bookId, categoryId) VALUES (122, 2);
INSERT INTO BookCategory (bookId, categoryId) VALUES (122, 3);
INSERT INTO BookCategory (bookId, categoryId) VALUES (122, 16);
INSERT INTO BookCategory (bookId, categoryId) VALUES (123, 2);
INSERT INTO BookCategory (bookId, categoryId) VALUES (123, 3);
INSERT INTO BookCategory (bookId, categoryId) VALUES (123, 16);
INSERT INTO BookCategory (bookId, categoryId) VALUES (124, 2);
INSERT INTO BookCategory (bookId, categoryId) VALUES (124, 3);
INSERT INTO BookCategory (bookId, categoryId) VALUES (124, 16);
INSERT INTO BookCategory (bookId, categoryId) VALUES (125, 2);
INSERT INTO BookCategory (bookId, categoryId) VALUES (125, 3);
INSERT INTO BookCategory (bookId, categoryId) VALUES (125, 16);
INSERT INTO BookCategory (bookId, categoryId) VALUES (126, 2);
INSERT INTO BookCategory (bookId, categoryId) VALUES (126, 3);
INSERT INTO BookCategory (bookId, categoryId) VALUES (126, 16);
INSERT INTO BookCategory (bookId, categoryId) VALUES (127, 2);
INSERT INTO BookCategory (bookId, categoryId) VALUES (127, 3);
INSERT INTO BookCategory (bookId, categoryId) VALUES (127, 16);
INSERT INTO BookCategory (bookId, categoryId) VALUES (128, 2);
INSERT INTO BookCategory (bookId, categoryId) VALUES (128, 3);
INSERT INTO BookCategory (bookId, categoryId) VALUES (128, 16);
INSERT INTO BookCategory (bookId, categoryId) VALUES (129, 2);
INSERT INTO BookCategory (bookId, categoryId) VALUES (129, 3);
INSERT INTO BookCategory (bookId, categoryId) VALUES (129, 16);
INSERT INTO BookCategory (bookId, categoryId) VALUES (130, 2);
INSERT INTO BookCategory (bookId, categoryId) VALUES (130, 3);
INSERT INTO BookCategory (bookId, categoryId) VALUES (130, 16);
INSERT INTO BookCategory (bookId, categoryId) VALUES (131, 2);
INSERT INTO BookCategory (bookId, categoryId) VALUES (131, 3);
INSERT INTO BookCategory (bookId, categoryId) VALUES (131, 16);
INSERT INTO BookCategory (bookId, categoryId) VALUES (146, 3);
INSERT INTO BookCategory (bookId, categoryId) VALUES (146, 16);
INSERT INTO BookCategory (bookId, categoryId) VALUES (147, 3);
INSERT INTO BookCategory (bookId, categoryId) VALUES (147, 16);
INSERT INTO BookCategory (bookId, categoryId) VALUES (148, 3);
INSERT INTO BookCategory (bookId, categoryId) VALUES (148, 16);
INSERT INTO BookCategory (bookId, categoryId) VALUES (149, 3);
INSERT INTO BookCategory (bookId, categoryId) VALUES (149, 16);
INSERT INTO BookCategory (bookId, categoryId) VALUES (93, 2);
INSERT INTO BookCategory (bookId, categoryId) VALUES (93, 3);
INSERT INTO BookCategory (bookId, categoryId) VALUES (93, 7);
INSERT INTO BookCategory (bookId, categoryId) VALUES (93, 16);
INSERT INTO BookCategory (bookId, categoryId) VALUES (94, 2);
INSERT INTO BookCategory (bookId, categoryId) VALUES (94, 3);
INSERT INTO BookCategory (bookId, categoryId) VALUES (94, 7);
INSERT INTO BookCategory (bookId, categoryId) VALUES (94, 16);
INSERT INTO BookCategory (bookId, categoryId) VALUES (95, 2);
INSERT INTO BookCategory (bookId, categoryId) VALUES (95, 3);
INSERT INTO BookCategory (bookId, categoryId) VALUES (95, 7);
INSERT INTO BookCategory (bookId, categoryId) VALUES (95, 16);
INSERT INTO BookCategory (bookId, categoryId) VALUES (96, 2);
INSERT INTO BookCategory (bookId, categoryId) VALUES (96, 3);
INSERT INTO BookCategory (bookId, categoryId) VALUES (96, 7);
INSERT INTO BookCategory (bookId, categoryId) VALUES (96, 16);
INSERT INTO BookCategory (bookId, categoryId) VALUES (97, 2);
INSERT INTO BookCategory (bookId, categoryId) VALUES (97, 3);
INSERT INTO BookCategory (bookId, categoryId) VALUES (97, 7);
INSERT INTO BookCategory (bookId, categoryId) VALUES (97, 16);
INSERT INTO BookCategory (bookId, categoryId) VALUES (98, 2);
INSERT INTO BookCategory (bookId, categoryId) VALUES (98, 3);
INSERT INTO BookCategory (bookId, categoryId) VALUES (98, 7);
INSERT INTO BookCategory (bookId, categoryId) VALUES (98, 16);
INSERT INTO BookCategory (bookId, categoryId) VALUES (99, 2);
INSERT INTO BookCategory (bookId, categoryId) VALUES (99, 3);
INSERT INTO BookCategory (bookId, categoryId) VALUES (99, 7);
INSERT INTO BookCategory (bookId, categoryId) VALUES (99, 16);
INSERT INTO BookCategory (bookId, categoryId) VALUES (76, 8);
INSERT INTO BookCategory (bookId, categoryId) VALUES (45, 11);
INSERT INTO BookCategory (bookId, categoryId) VALUES (46, 11);
INSERT INTO BookCategory (bookId, categoryId) VALUES (47, 11);
INSERT INTO BookCategory (bookId, categoryId) VALUES (48, 11);
INSERT INTO BookCategory (bookId, categoryId) VALUES (39, 20);
INSERT INTO BookCategory (bookId, categoryId) VALUES (40, 20);
INSERT INTO BookCategory (bookId, categoryId) VALUES (156, 8);
INSERT INTO BookCategory (bookId, categoryId) VALUES (25, 8);
INSERT INTO BookCategory (bookId, categoryId) VALUES (81, 4);
INSERT INTO BookCategory (bookId, categoryId) VALUES (31, 8);
INSERT INTO BookCategory (bookId, categoryId) VALUES (157, 6);
INSERT INTO BookCategory (bookId, categoryId) VALUES (158, 6);
INSERT INTO BookCategory (bookId, categoryId) VALUES (77, 8);
INSERT INTO BookCategory (bookId, categoryId) VALUES (38, 11);
INSERT INTO BookCategory (bookId, categoryId) VALUES (132, 11);
INSERT INTO BookCategory (bookId, categoryId) VALUES (133, 12);
INSERT INTO BookCategory (bookId, categoryId) VALUES (10, 8);
INSERT INTO BookCategory (bookId, categoryId) VALUES (6, 21);
INSERT INTO BookCategory (bookId, categoryId) VALUES (6, 23);
INSERT INTO BookCategory (bookId, categoryId) VALUES (7, 21);
INSERT INTO BookCategory (bookId, categoryId) VALUES (7, 23);
INSERT INTO BookCategory (bookId, categoryId) VALUES (41, 8);
INSERT INTO BookCategory (bookId, categoryId) VALUES (26, 8);
INSERT INTO BookCategory (bookId, categoryId) VALUES (82, 8);
INSERT INTO BookCategory (bookId, categoryId) VALUES (82, 21);
INSERT INTO BookCategory (bookId, categoryId) VALUES (106, 22);
INSERT INTO BookCategory (bookId, categoryId) VALUES (1, 8);
INSERT INTO BookCategory (bookId, categoryId) VALUES (1, 18);
INSERT INTO BookCategory (bookId, categoryId) VALUES (12, 11);
INSERT INTO BookCategory (bookId, categoryId) VALUES (13, 11);
INSERT INTO BookCategory (bookId, categoryId) VALUES (2, 8);
INSERT INTO BookCategory (bookId, categoryId) VALUES (2, 18);
INSERT INTO BookCategory (bookId, categoryId) VALUES (27, 8);
INSERT INTO BookCategory (bookId, categoryId) VALUES (9, 8);
INSERT INTO BookCategory (bookId, categoryId) VALUES (9, 18);
INSERT INTO BookCategory (bookId, categoryId) VALUES (134, 11);
INSERT INTO BookCategory (bookId, categoryId) VALUES (14, 11);
INSERT INTO BookCategory (bookId, categoryId) VALUES (118, 2);
INSERT INTO BookCategory (bookId, categoryId) VALUES (118, 10);
INSERT INTO BookCategory (bookId, categoryId) VALUES (118, 19);
INSERT INTO BookCategory (bookId, categoryId) VALUES (78, 8);
INSERT INTO BookCategory (bookId, categoryId) VALUES (84, 5);
INSERT INTO BookCategory (bookId, categoryId) VALUES (85, 8);
INSERT INTO BookCategory (bookId, categoryId) VALUES (86, 8);
INSERT INTO BookCategory (bookId, categoryId) VALUES (87, 8);
INSERT INTO BookCategory (bookId, categoryId) VALUES (107, 8);
INSERT INTO BookCategory (bookId, categoryId) VALUES (11, 8);
INSERT INTO BookCategory (bookId, categoryId) VALUES (119, 2);
INSERT INTO BookCategory (bookId, categoryId) VALUES (119, 8);
INSERT INTO BookCategory (bookId, categoryId) VALUES (119, 10);
INSERT INTO BookCategory (bookId, categoryId) VALUES (120, 2);
INSERT INTO BookCategory (bookId, categoryId) VALUES (120, 8);
INSERT INTO BookCategory (bookId, categoryId) VALUES (120, 10);
INSERT INTO BookCategory (bookId, categoryId) VALUES (28, 8);
INSERT INTO BookCategory (bookId, categoryId) VALUES (15, 3);
INSERT INTO BookCategory (bookId, categoryId) VALUES (15, 16);
INSERT INTO BookCategory (bookId, categoryId) VALUES (16, 3);
INSERT INTO BookCategory (bookId, categoryId) VALUES (16, 16);
INSERT INTO BookCategory (bookId, categoryId) VALUES (17, 3);
INSERT INTO BookCategory (bookId, categoryId) VALUES (17, 16);
INSERT INTO BookCategory (bookId, categoryId) VALUES (18, 3);
INSERT INTO BookCategory (bookId, categoryId) VALUES (18, 16);
INSERT INTO BookCategory (bookId, categoryId) VALUES (19, 3);
INSERT INTO BookCategory (bookId, categoryId) VALUES (19, 16);
INSERT INTO BookCategory (bookId, categoryId) VALUES (20, 3);
INSERT INTO BookCategory (bookId, categoryId) VALUES (20, 16);
INSERT INTO BookCategory (bookId, categoryId) VALUES (21, 3);
INSERT INTO BookCategory (bookId, categoryId) VALUES (21, 16);
INSERT INTO BookCategory (bookId, categoryId) VALUES (22, 3);
INSERT INTO BookCategory (bookId, categoryId) VALUES (22, 16);
INSERT INTO BookCategory (bookId, categoryId) VALUES (23, 3);
INSERT INTO BookCategory (bookId, categoryId) VALUES (23, 16);
INSERT INTO BookCategory (bookId, categoryId) VALUES (24, 3);
INSERT INTO BookCategory (bookId, categoryId) VALUES (24, 16);
INSERT INTO BookCategory (bookId, categoryId) VALUES (109, 2);
INSERT INTO BookCategory (bookId, categoryId) VALUES (109, 3);
INSERT INTO BookCategory (bookId, categoryId) VALUES (109, 16);
INSERT INTO BookCategory (bookId, categoryId) VALUES (110, 2);
INSERT INTO BookCategory (bookId, categoryId) VALUES (110, 3);
INSERT INTO BookCategory (bookId, categoryId) VALUES (110, 16);
INSERT INTO BookCategory (bookId, categoryId) VALUES (111, 2);
INSERT INTO BookCategory (bookId, categoryId) VALUES (111, 3);
INSERT INTO BookCategory (bookId, categoryId) VALUES (111, 16);
INSERT INTO BookCategory (bookId, categoryId) VALUES (150, 3);
INSERT INTO BookCategory (bookId, categoryId) VALUES (150, 16);
INSERT INTO BookCategory (bookId, categoryId) VALUES (151, 3);
INSERT INTO BookCategory (bookId, categoryId) VALUES (151, 16);
INSERT INTO BookCategory (bookId, categoryId) VALUES (53, 8);
INSERT INTO BookCategory (bookId, categoryId) VALUES (112, 3);
INSERT INTO BookCategory (bookId, categoryId) VALUES (112, 16);
INSERT INTO BookCategory (bookId, categoryId) VALUES (113, 3);
INSERT INTO BookCategory (bookId, categoryId) VALUES (113, 16);
INSERT INTO BookCategory (bookId, categoryId) VALUES (114, 23);
INSERT INTO BookCategory (bookId, categoryId) VALUES (115, 23);
INSERT INTO BookCategory (bookId, categoryId) VALUES (116, 23);
INSERT INTO BookCategory (bookId, categoryId) VALUES (117, 23);
INSERT INTO BookCategory (bookId, categoryId) VALUES (42, 8);
INSERT INTO BookCategory (bookId, categoryId) VALUES (49, 11);
INSERT INTO BookCategory (bookId, categoryId) VALUES (3, 8);
INSERT INTO BookCategory (bookId, categoryId) VALUES (3, 18);
INSERT INTO BookCategory (bookId, categoryId) VALUES (54, 19);
INSERT INTO BookCategory (bookId, categoryId) VALUES (29, 8);
INSERT INTO BookCategory (bookId, categoryId) VALUES (135, 8);
INSERT INTO BookCategory (bookId, categoryId) VALUES (135, 9);
INSERT INTO BookCategory (bookId, categoryId) VALUES (43, 20);
INSERT INTO BookCategory (bookId, categoryId) VALUES (44, 8);
INSERT INTO BookCategory (bookId, categoryId) VALUES (136, 8);
INSERT INTO BookCategory (bookId, categoryId) VALUES (136, 9);
INSERT INTO BookCategory (bookId, categoryId) VALUES (55, 8);
INSERT INTO BookCategory (bookId, categoryId) VALUES (79, 8);
INSERT INTO BookCategory (bookId, categoryId) VALUES (88, 8);
INSERT INTO BookCategory (bookId, categoryId) VALUES (89, 8);
INSERT INTO BookCategory (bookId, categoryId) VALUES (90, 8);
INSERT INTO BookCategory (bookId, categoryId) VALUES (50, 11);
INSERT INTO BookCategory (bookId, categoryId) VALUES (56, 8);
INSERT INTO BookCategory (bookId, categoryId) VALUES (57, 8);
INSERT INTO BookCategory (bookId, categoryId) VALUES (58, 8);
INSERT INTO BookCategory (bookId, categoryId) VALUES (59, 8);
INSERT INTO BookCategory (bookId, categoryId) VALUES (60, 8);
INSERT INTO BookCategory (bookId, categoryId) VALUES (61, 8);
INSERT INTO BookCategory (bookId, categoryId) VALUES (51, 11);
INSERT INTO BookCategory (bookId, categoryId) VALUES (137, 8);
INSERT INTO BookCategory (bookId, categoryId) VALUES (137, 9);
INSERT INTO BookCategory (bookId, categoryId) VALUES (138, 8);
INSERT INTO BookCategory (bookId, categoryId) VALUES (138, 9);
INSERT INTO BookCategory (bookId, categoryId) VALUES (62, 8);
INSERT INTO BookCategory (bookId, categoryId) VALUES (63, 8);
INSERT INTO BookCategory (bookId, categoryId) VALUES (91, 8);
INSERT INTO BookCategory (bookId, categoryId) VALUES (64, 8);
INSERT INTO BookCategory (bookId, categoryId) VALUES (65, 8);
INSERT INTO BookCategory (bookId, categoryId) VALUES (66, 7);
INSERT INTO BookCategory (bookId, categoryId) VALUES (67, 8);
INSERT INTO BookCategory (bookId, categoryId) VALUES (8, 21);
INSERT INTO BookCategory (bookId, categoryId) VALUES (8, 23);
INSERT INTO BookCategory (bookId, categoryId) VALUES (139, 3);
INSERT INTO BookCategory (bookId, categoryId) VALUES (4, 8);
INSERT INTO BookCategory (bookId, categoryId) VALUES (4, 18);
INSERT INTO BookCategory (bookId, categoryId) VALUES (5, 8);
INSERT INTO BookCategory (bookId, categoryId) VALUES (5, 18);
INSERT INTO BookCategory (bookId, categoryId) VALUES (68, 8);
INSERT INTO BookCategory (bookId, categoryId) VALUES (159, 6);
INSERT INTO BookCategory (bookId, categoryId) VALUES (80, 8);
INSERT INTO BookCategory (bookId, categoryId) VALUES (69, 8);
INSERT INTO BookCategory (bookId, categoryId) VALUES (92, 8);
INSERT INTO BookCategory (bookId, categoryId) VALUES (70, 8);
INSERT INTO BookCategory (bookId, categoryId) VALUES (71, 8);
INSERT INTO BookCategory (bookId, categoryId) VALUES (32, 8);
INSERT INTO BookCategory (bookId, categoryId) VALUES (72, 8);
INSERT INTO BookCategory (bookId, categoryId) VALUES (52, 11);
INSERT INTO BookCategory (bookId, categoryId) VALUES (73, 8);
INSERT INTO BookCategory (bookId, categoryId) VALUES (74, 8);
INSERT INTO BookCategory (bookId, categoryId) VALUES (75, 8);
INSERT INTO BookCategory (bookId, categoryId) VALUES (140, 3);
INSERT INTO BookCategory (bookId, categoryId) VALUES (161, 8);
INSERT INTO BookCategory (bookId, categoryId) VALUES (162, 8);
INSERT INTO BookCategory (bookId, categoryId) VALUES (163, 8);
INSERT INTO BookCategory (bookId, categoryId) VALUES (164, 8);
INSERT INTO BookCategory (bookId, categoryId) VALUES (165, 8);
INSERT INTO BookCategory (bookId, categoryId) VALUES (121, 2);
INSERT INTO BookCategory (bookId, categoryId) VALUES (121, 10);
INSERT INTO BookCategory (bookId, categoryId) VALUES (121, 19);
INSERT INTO BookCategory (bookId, categoryId) VALUES (35, 3);
INSERT INTO BookCategory (bookId, categoryId) VALUES (36, 3);
INSERT INTO BookCategory (bookId, categoryId) VALUES (37, 3);
INSERT INTO BookCategory (bookId, categoryId) VALUES (33, 8);
INSERT INTO BookCategory (bookId, categoryId) VALUES (108, 17);
INSERT INTO BookCategory (bookId, categoryId) VALUES (34, 8);
INSERT INTO BookCategory (bookId, categoryId) VALUES (166, 6);
INSERT INTO BookCategory (bookId, categoryId) VALUES (100, 3);
INSERT INTO BookCategory (bookId, categoryId) VALUES (100, 16);
INSERT INTO BookCategory (bookId, categoryId) VALUES (101, 3);
INSERT INTO BookCategory (bookId, categoryId) VALUES (101, 16);
INSERT INTO BookCategory (bookId, categoryId) VALUES (102, 3);
INSERT INTO BookCategory (bookId, categoryId) VALUES (102, 16);
INSERT INTO BookCategory (bookId, categoryId) VALUES (103, 3);
INSERT INTO BookCategory (bookId, categoryId) VALUES (103, 16);
INSERT INTO BookCategory (bookId, categoryId) VALUES (104, 3);
INSERT INTO BookCategory (bookId, categoryId) VALUES (104, 16);

INSERT INTO Account (id, phone, password, name, gender, email, address, isAdmin, isLocked) VALUES (1, '1234567810', '$2a$10$kYtHn.cSSmb3C/YXMi3fGuCb0Wq/.8KWFwE0/2ptd568T5MN08li2', 'Graehme Giffon', 'Other', 'ggiffon0@wired.com', '28 Pine View Hill', 'true', 'false');
INSERT INTO Account (id, phone, password, name, gender, email, address, isAdmin, isLocked) VALUES (11, '1234567801', '$2a$10$F8T1jrMzMW.jHNRilCP2NOerVjIQxlW48AFUB2FPRPMJ4e2YnPng6', 'Rhona Lightowlers', 'Other', 'rlightowlersa@adobe.com', '362 Gerald Crossing', 'false', 'true');
INSERT INTO Account (id, phone, password, name, gender, email, address, isAdmin, isLocked) VALUES (3, '1234567800', '$2a$10$PJF/3pPBXSysWAXPDi0fV./s9BJcc.2whUAZ4qqxNGkYPUKdYY39K', 'Melony Hulcoop', 'Female', 'mhulcoop2@nytimes.com', '35217 Schurz Parkway', 'false', 'false');
INSERT INTO Account (id, phone, password, name, gender, email, address, isAdmin, isLocked) VALUES (12, '1234567811', '$2a$10$3xPIpiYMZiHehFQFh0mocOAkUtFH/mDEc.P5C6zMo8DkAgJUnaWk6', 'Robin Attaway', 'Other', 'rattawayb@cbc.ca', '8 Carey Trail', 'true', 'true');

INSERT INTO Account (id, phone, password, name, gender, email, address, isAdmin, isLocked) VALUES (2, '4826548630', 'gE7,b', 'Alane McCullouch', 'Female', 'amccullouch1@imgur.com', '3 Sugar Plaza', 'true', 'false');
INSERT INTO Account (id, phone, password, name, gender, email, address, isAdmin, isLocked) VALUES (4, '6227898268', 'uH4#(=j./', 'Lenard Ateridge', 'Female', 'lateridge3@mozilla.org', '20 Glacier Hill Avenue', 'false', 'false');
INSERT INTO Account (id, phone, password, name, gender, email, address, isAdmin, isLocked) VALUES (5, '7393193348', 'tX7#|g#LP', 'Casi Ainley', 'Female', 'cainley4@redcross.org', '89908 Summit Plaza', 'false', 'false');
INSERT INTO Account (id, phone, password, name, gender, email, address, isAdmin, isLocked) VALUES (6, '6251725090', 'fL2@mqkw|Z', 'Giacinta Coey', 'Male', 'gcoey5@noaa.gov', '2 Upham Junction', 'false', 'false');
INSERT INTO Account (id, phone, password, name, gender, email, address, isAdmin, isLocked) VALUES (7, '8321027390', 'gW4/%', 'Ilka Geeves', 'Other', 'igeeves6@odnoklassniki.ru', '32 Sundown Park', 'false', 'false');
INSERT INTO Account (id, phone, password, name, gender, email, address, isAdmin, isLocked) VALUES (8, '4654441932', 'kL7)IMr#', 'Angel Annakin', 'Other', 'aannakin7@wired.com', '58990 Ronald Regan Street', 'false', 'false');
INSERT INTO Account (id, phone, password, name, gender, email, address, isAdmin, isLocked) VALUES (9, '9138407685', 'mJ4@DzD', 'Dalton Thornbarrow', 'Other', 'dthornbarrow8@statcounter.com', '120 Mariners Cove Crossing', 'false', 'false');
INSERT INTO Account (id, phone, password, name, gender, email, address, isAdmin, isLocked) VALUES (10, '3168247064', 'zM2.''8mw', 'Laina Rennock', 'Male', 'lrennock9@tiny.cc', '556 Duke Pass', 'false', 'true');

INSERT INTO Member (id, phone, name, gender, dateOfBirth, email, address) VALUES (1, '3123467423', 'Umeko Cottisford', 'Male', '2012-06-28', 'ucottisford0@wp.com', NULL);
INSERT INTO Member (id, phone, name, gender, dateOfBirth, email, address) VALUES (2, '2996009524', 'Dehlia Scotchmoor', 'Other', '2003-07-17', 'dscotchmoor1@blog.com', NULL);
INSERT INTO Member (id, phone, name, gender, dateOfBirth, email, address) VALUES (3, '4724008955', 'Sarajane Keslake', 'Female', '1994-04-08', 'skeslake2@angelfire.com', '0 Weeping Birch Pass');
INSERT INTO Member (id, phone, name, gender, dateOfBirth, email, address) VALUES (4, '5425382204', 'Ronny Sagar', 'Female', '1971-09-17', 'rsagar3@cisco.com', '9 Rigney Center');
INSERT INTO Member (id, phone, name, gender, dateOfBirth, email, address) VALUES (5, '2509039072', 'Ferne Harkes', 'Male', '1993-01-15', 'fharkes4@nifty.com', '13681 Shopko Junction');
INSERT INTO Member (id, phone, name, gender, dateOfBirth, email, address) VALUES (6, '5041319126', 'Chauncey Ratchford', 'Other', '1990-07-19', 'cratchford5@arstechnica.com', NULL);
INSERT INTO Member (id, phone, name, gender, dateOfBirth, email, address) VALUES (7, '5532593414', 'Pris Rannie', 'Male', '1973-12-16', 'prannie6@ezinearticles.com', '23061 5th Street');
INSERT INTO Member (id, phone, name, gender, dateOfBirth, email, address) VALUES (8, '8028209393', 'Sibylle Boon', 'Other', '2010-07-17', NULL, '4137 Center Junction');
INSERT INTO Member (id, phone, name, gender, dateOfBirth, email, address) VALUES (9, '1497997603', 'Ulrich Buttriss', 'Female', '1999-11-11', NULL, '6176 Maple Street');
INSERT INTO Member (id, phone, name, gender, dateOfBirth, email, address) VALUES (10, '8487711903', 'Trueman Hedde', 'Other', '1976-02-25', 'thedde9@ovh.net', '355 Haas Alley');
INSERT INTO Member (id, phone, name, gender, dateOfBirth, email, address) VALUES (11, '4459909559', 'Raffarty Costan', 'Other', '1987-05-11', 'rcostana@google.ca', NULL);
INSERT INTO Member (id, phone, name, gender, dateOfBirth, email, address) VALUES (12, '5159757172', 'Jeno Vise', 'Female', '1973-08-30', 'jviseb@mac.com', '223 Anniversary Junction');
INSERT INTO Member (id, phone, name, gender, dateOfBirth, email, address) VALUES (13, '1288763709', 'Erminie Corwood', 'Other', '1993-11-04', NULL, '1 Northfield Parkway');
INSERT INTO Member (id, phone, name, gender, dateOfBirth, email, address) VALUES (14, '1561575179', 'Maribeth Laverack', 'Other', '2014-05-29', 'mlaverackd@prweb.com', NULL);
INSERT INTO Member (id, phone, name, gender, dateOfBirth, email, address) VALUES (15, '2353666435', 'Arlin Duddell', 'Other', '1977-03-14', 'aduddelle@comsenz.com', NULL);
INSERT INTO Member (id, phone, name, gender, dateOfBirth, email, address) VALUES (16, '7967049902', 'Stearne Biddiss', 'Male', '1999-08-25', 'sbiddissf@ihg.com', '40 Karstens Crossing');
INSERT INTO Member (id, phone, name, gender, dateOfBirth, email, address) VALUES (17, '5477920851', 'Clementia Sowood', 'Other', '1982-03-05', 'csowoodg@thetimes.co.uk', '3 Fairview Drive');
INSERT INTO Member (id, phone, name, gender, dateOfBirth, email, address) VALUES (18, '2477693916', 'Brigham Crewe', 'Female', '1976-10-07', 'bcreweh@fastcompany.com', NULL);
INSERT INTO Member (id, phone, name, gender, dateOfBirth, email, address) VALUES (19, '4058241637', 'Osbert Jann', 'Male', '1986-11-18', 'ojanni@addthis.com', '558 Moulton Crossing');
INSERT INTO Member (id, phone, name, gender, dateOfBirth, email, address) VALUES (20, '2612250598', 'Lyn Lambrook', 'Other', '1981-03-22', 'llambrookj@vk.com', '2647 Transport Drive');
INSERT INTO Member (id, phone, name, gender, dateOfBirth, email, address) VALUES (21, '6528823914', 'Waite Bernaldez', 'Male', '1993-04-04', 'wbernaldezk@angelfire.com', '13903 Northfield Circle');
INSERT INTO Member (id, phone, name, gender, dateOfBirth, email, address) VALUES (22, '3563129620', 'Felix Dennidge', 'Male', '1970-03-27', NULL, '687 Hudson Parkway');
INSERT INTO Member (id, phone, name, gender, dateOfBirth, email, address) VALUES (23, '7589266514', 'Ronnie Swindley', 'Other', '2004-07-21', 'rswindleym@spiegel.de', '942 Toban Circle');
INSERT INTO Member (id, phone, name, gender, dateOfBirth, email, address) VALUES (24, '8223537299', 'Madeleine Ballsdon', 'Male', '2014-08-14', NULL, '89970 Pond Point');
INSERT INTO Member (id, phone, name, gender, dateOfBirth, email, address) VALUES (25, '4983848863', 'Skipton Stabler', 'Other', '2014-12-08', 'sstablero@shinystat.com', NULL);
INSERT INTO Member (id, phone, name, gender, dateOfBirth, email, address) VALUES (26, '3904676139', 'Cynthea Izkovitz', 'Male', '1992-11-02', 'cizkovitzp@house.gov', '0 Lighthouse Bay Avenue');
INSERT INTO Member (id, phone, name, gender, dateOfBirth, email, address) VALUES (27, '2415025434', 'Read Buckenham', 'Female', '2008-07-14', 'rbuckenhamq@vimeo.com', NULL);
INSERT INTO Member (id, phone, name, gender, dateOfBirth, email, address) VALUES (28, '9705493664', 'Olimpia Alans', 'Female', '2003-05-09', 'oalansr@pagesperso-orange.fr', '678 Michigan Avenue');
INSERT INTO Member (id, phone, name, gender, dateOfBirth, email, address) VALUES (29, '9656370320', 'Wynn Gaynor', 'Male', '2003-04-05', 'wgaynors@arstechnica.com', NULL);
INSERT INTO Member (id, phone, name, gender, dateOfBirth, email, address) VALUES (30, '5624482251', 'Junette Harle', 'Female', '1984-09-19', 'jharlet@facebook.com', '5 Roxbury Parkway');
INSERT INTO Member (id, phone, name, gender, dateOfBirth, email, address) VALUES (31, '6602913960', 'Lee Blooman', 'Other', '2015-12-09', 'lbloomanu@mozilla.org', '6 Spenser Circle');
INSERT INTO Member (id, phone, name, gender, dateOfBirth, email, address) VALUES (32, '4466204261', 'Meridel Pestell', 'Male', '1973-03-20', NULL, NULL);
INSERT INTO Member (id, phone, name, gender, dateOfBirth, email, address) VALUES (33, '9603911395', 'Fawne Chilley', 'Male', '1997-02-02', 'fchilleyw@shop-pro.jp', NULL);
INSERT INTO Member (id, phone, name, gender, dateOfBirth, email, address) VALUES (34, '7023178448', 'Reggis Gerraty', 'Other', '2014-07-17', 'rgerratyx@bbb.org', '47642 2nd Park');
INSERT INTO Member (id, phone, name, gender, dateOfBirth, email, address) VALUES (35, '6253976212', 'Heath Probey', 'Female', '1984-08-13', 'hprobeyy@phpbb.com', NULL);
INSERT INTO Member (id, phone, name, gender, dateOfBirth, email, address) VALUES (36, '3858226501', 'Rozele Edgcumbe', 'Other', '1982-10-07', 'redgcumbez@dagondesign.com', '98 Ludington Parkway');
INSERT INTO Member (id, phone, name, gender, dateOfBirth, email, address) VALUES (37, '6304846512', 'Tawnya Quilter', 'Other', '2012-02-29', 'tquilter10@instagram.com', '486 Warbler Street');
INSERT INTO Member (id, phone, name, gender, dateOfBirth, email, address) VALUES (38, '3136057266', 'Nestor Widdocks', 'Male', '1976-01-07', 'nwiddocks11@ox.ac.uk', '41 Randy Road');
INSERT INTO Member (id, phone, name, gender, dateOfBirth, email, address) VALUES (39, '8161211975', 'Shelly Klimek', 'Other', '1984-01-05', 'sklimek12@t.co', '204 Graedel Terrace');
INSERT INTO Member (id, phone, name, gender, dateOfBirth, email, address) VALUES (40, '5322389786', 'Clarie Vittore', 'Male', '1979-02-27', 'cvittore13@imgur.com', '845 Mendota Avenue');
INSERT INTO Member (id, phone, name, gender, dateOfBirth, email, address) VALUES (41, '3394034921', 'Stephi Catonne', 'Female', '1978-09-14', 'scatonne14@blinklist.com', NULL);
INSERT INTO Member (id, phone, name, gender, dateOfBirth, email, address) VALUES (42, '6479404761', 'Tome Laytham', 'Other', '1992-05-28', 'tlaytham15@sbwire.com', NULL);
INSERT INTO Member (id, phone, name, gender, dateOfBirth, email, address) VALUES (43, '7435369577', 'Saunders Mennithorp', 'Female', '1993-06-04', 'smennithorp16@archive.org', NULL);
INSERT INTO Member (id, phone, name, gender, dateOfBirth, email, address) VALUES (44, '3423844432', 'Dov Keddey', 'Female', '1982-01-07', 'dkeddey17@vistaprint.com', NULL);
INSERT INTO Member (id, phone, name, gender, dateOfBirth, email, address) VALUES (45, '2258813678', 'Ruperta Filipovic', 'Female', '1972-07-15', 'rfilipovic18@statcounter.com', NULL);
INSERT INTO Member (id, phone, name, gender, dateOfBirth, email, address) VALUES (46, '5358349299', 'Trisha Tarborn', 'Other', '1999-06-09', 'ttarborn19@google.com.br', '61866 Kinsman Circle');
INSERT INTO Member (id, phone, name, gender, dateOfBirth, email, address) VALUES (47, '4746987566', 'Andie Ansett', 'Male', '1988-05-09', 'aansett1a@squarespace.com', '74158 Elgar Point');
INSERT INTO Member (id, phone, name, gender, dateOfBirth, email, address) VALUES (48, '7019623126', 'Darice Stone Fewings', 'Other', '1998-10-13', 'dstone1b@sfgate.com', '7383 Beilfuss Terrace');
INSERT INTO Member (id, phone, name, gender, dateOfBirth, email, address) VALUES (49, '8609235101', 'Patsy Rissom', 'Other', '1975-12-23', 'prissom1c@php.net', NULL);
INSERT INTO Member (id, phone, name, gender, dateOfBirth, email, address) VALUES (50, '5319524057', 'Davine Pischoff', 'Male', '2001-07-09', NULL, '3476 Killdeer Point');
INSERT INTO Member (id, phone, name, gender, dateOfBirth, email, address) VALUES (51, '1695123708', 'Morton Elbourne', 'Female', '2006-04-04', 'melbourne1e@facebook.com', '61446 Forest Dale Plaza');
INSERT INTO Member (id, phone, name, gender, dateOfBirth, email, address) VALUES (52, '7064043581', 'Thalia Coleyshaw', 'Female', '2008-12-03', 'tcoleyshaw1f@e-recht24.de', NULL);
INSERT INTO Member (id, phone, name, gender, dateOfBirth, email, address) VALUES (53, '6615754184', 'Nero Craigheid', 'Male', '1974-07-08', 'ncraigheid1g@archive.org', '3778 Jenifer Pass');
INSERT INTO Member (id, phone, name, gender, dateOfBirth, email, address) VALUES (54, '3483195502', 'Rafferty Marlowe', 'Male', '1999-04-19', 'rmarlowe1h@istockphoto.com', NULL);
INSERT INTO Member (id, phone, name, gender, dateOfBirth, email, address) VALUES (55, '5299128230', 'Roi Tolworthie', 'Male', '1977-06-22', 'rtolworthie1i@barnesandnoble.com', '69 Amoth Terrace');
INSERT INTO Member (id, phone, name, gender, dateOfBirth, email, address) VALUES (56, '6681721718', 'Rachel Kivelhan', 'Other', '1990-01-13', 'rkivelhan1j@joomla.org', '185 Londonderry Court');
INSERT INTO Member (id, phone, name, gender, dateOfBirth, email, address) VALUES (57, '9063547957', 'Honoria Muddiman', 'Male', '2015-06-11', 'hmuddiman1k@amazon.com', '974 Dovetail Junction');
INSERT INTO Member (id, phone, name, gender, dateOfBirth, email, address) VALUES (58, '5779736161', 'Tiffi Heeley', 'Male', '1983-06-23', 'theeley1l@cargocollective.com', '3 Nova Circle');
INSERT INTO Member (id, phone, name, gender, dateOfBirth, email, address) VALUES (59, '8433802866', 'Carny Piche', 'Male', '1976-09-20', 'cpiche1m@opera.com', '8 5th Street');
INSERT INTO Member (id, phone, name, gender, dateOfBirth, email, address) VALUES (60, '8763407518', 'Raddy Mardell', 'Female', '1972-10-06', 'rmardell1n@nymag.com', '729 Reindahl Park');
INSERT INTO Member (id, phone, name, gender, dateOfBirth, email, address) VALUES (61, '8146210995', 'Aggi Cathesyed', 'Female', '2015-09-26', 'acathesyed1o@state.gov', NULL);
INSERT INTO Member (id, phone, name, gender, dateOfBirth, email, address) VALUES (62, '4834442434', 'Amabelle Guest', 'Male', '1979-09-04', 'aguest1p@ihg.com', NULL);
INSERT INTO Member (id, phone, name, gender, dateOfBirth, email, address) VALUES (63, '6663968023', 'Kirbee Skatcher', 'Female', '2003-07-31', 'kskatcher1q@baidu.com', '695 Dennis Terrace');
INSERT INTO Member (id, phone, name, gender, dateOfBirth, email, address) VALUES (64, '9765435242', 'Tull Gealy', 'Female', '1977-04-24', 'tgealy1r@shutterfly.com', '39232 Beilfuss Court');
INSERT INTO Member (id, phone, name, gender, dateOfBirth, email, address) VALUES (65, '3633485178', 'Norrie Wegener', 'Other', '1976-03-02', 'nwegener1s@technorati.com', NULL);
INSERT INTO Member (id, phone, name, gender, dateOfBirth, email, address) VALUES (66, '4917210050', 'Harald Bilston', 'Male', '1993-08-15', 'hbilston1t@hc360.com', '57 Pepper Wood Point');
INSERT INTO Member (id, phone, name, gender, dateOfBirth, email, address) VALUES (67, '5663997741', 'Cordelie Wakefield', 'Female', '2005-02-16', 'cwakefield1u@people.com.cn', '7768 Ilene Place');
INSERT INTO Member (id, phone, name, gender, dateOfBirth, email, address) VALUES (68, '9445522830', 'Kali Catteroll', 'Other', '1993-02-09', 'kcatteroll1v@exblog.jp', NULL);
INSERT INTO Member (id, phone, name, gender, dateOfBirth, email, address) VALUES (69, '8662391082', 'Wilek Whittles', 'Other', '1988-11-04', 'wwhittles1w@ameblo.jp', NULL);
INSERT INTO Member (id, phone, name, gender, dateOfBirth, email, address) VALUES (70, '2036644539', 'Sephira Gellately', 'Other', '1981-05-14', 'sgellately1x@webnode.com', NULL);
INSERT INTO Member (id, phone, name, gender, dateOfBirth, email, address) VALUES (71, '8361379567', 'Meaghan Piell', 'Female', '1991-10-22', 'mpiell1y@msu.edu', NULL);
INSERT INTO Member (id, phone, name, gender, dateOfBirth, email, address) VALUES (72, '6791378878', 'Chiquita Checketts', 'Female', '1971-04-28', 'cchecketts1z@theatlantic.com', '196 Bunting Terrace');
INSERT INTO Member (id, phone, name, gender, dateOfBirth, email, address) VALUES (73, '8467342520', 'Hakeem Durak', 'Other', '2010-12-15', 'hdurak20@kickstarter.com', '8 Merchant Drive');
INSERT INTO Member (id, phone, name, gender, dateOfBirth, email, address) VALUES (74, '1028496199', 'Illa Iacomini', 'Female', '2006-12-26', 'iiacomini21@pcworld.com', '30 Little Fleur Alley');
INSERT INTO Member (id, phone, name, gender, dateOfBirth, email, address) VALUES (75, '9185788436', 'Artur Carley', 'Other', '1991-01-22', 'acarley22@xinhuanet.com', '975 Menomonie Point');
INSERT INTO Member (id, phone, name, gender, dateOfBirth, email, address) VALUES (76, '5397764426', 'Kevin Sugars', 'Other', '2003-07-26', 'ksugars23@geocities.jp', '3021 Dennis Terrace');
INSERT INTO Member (id, phone, name, gender, dateOfBirth, email, address) VALUES (77, '4503949488', 'Joanie Cloutt', 'Female', '1987-12-05', 'jcloutt24@imdb.com', NULL);
INSERT INTO Member (id, phone, name, gender, dateOfBirth, email, address) VALUES (78, '8835211233', 'Rowena Eldin', 'Other', '1975-10-19', 'reldin25@hc360.com', NULL);
INSERT INTO Member (id, phone, name, gender, dateOfBirth, email, address) VALUES (79, '5933698794', 'Horten Dyhouse', 'Female', '1976-05-30', 'hdyhouse26@nymag.com', '332 Sugar Parkway');
INSERT INTO Member (id, phone, name, gender, dateOfBirth, email, address) VALUES (80, '1657083387', 'Bibbye Mohamed', 'Other', '1996-02-03', 'bmohamed27@princeton.edu', '467 Mendota Junction');
INSERT INTO Member (id, phone, name, gender, dateOfBirth, email, address) VALUES (81, '3557879691', 'Cleavland Kenion', 'Other', '1975-09-30', 'ckenion28@bbc.co.uk', '0424 Hazelcrest Avenue');
INSERT INTO Member (id, phone, name, gender, dateOfBirth, email, address) VALUES (82, '9143119992', 'Dorthea Mainwaring', 'Other', '1983-08-28', 'dmainwaring29@shinystat.com', NULL);
INSERT INTO Member (id, phone, name, gender, dateOfBirth, email, address) VALUES (83, '4796482460', 'Kerry Schellig', 'Male', '1982-06-04', 'kschellig2a@people.com.cn', '20775 Luster Crossing');
INSERT INTO Member (id, phone, name, gender, dateOfBirth, email, address) VALUES (84, '7647883756', 'Manolo Elliston', 'Female', '2007-10-28', 'melliston2b@free.fr', '634 Beilfuss Circle');
INSERT INTO Member (id, phone, name, gender, dateOfBirth, email, address) VALUES (85, '5115105393', 'La verne Skeldinge', 'Male', '1970-01-23', 'lverne2c@livejournal.com', '69536 Ryan Terrace');
INSERT INTO Member (id, phone, name, gender, dateOfBirth, email, address) VALUES (86, '7608044772', 'Shell Sayre', 'Female', '2003-08-23', 'ssayre2d@yolasite.com', '6 Kinsman Terrace');
INSERT INTO Member (id, phone, name, gender, dateOfBirth, email, address) VALUES (87, '6943475570', 'Innis Grizard', 'Female', '1990-08-30', 'igrizard2e@lycos.com', NULL);
INSERT INTO Member (id, phone, name, gender, dateOfBirth, email, address) VALUES (88, '8155490063', 'Taite Northen', 'Other', '2011-05-20', 'tnorthen2f@ox.ac.uk', '14 Hansons Avenue');
INSERT INTO Member (id, phone, name, gender, dateOfBirth, email, address) VALUES (89, '1427741032', 'Alikee Osgordby', 'Female', '1971-10-18', 'aosgordby2g@domainmarket.com', '356 Hayes Lane');
INSERT INTO Member (id, phone, name, gender, dateOfBirth, email, address) VALUES (90, '5705569831', 'Hayley Spillman', 'Male', '1977-04-26', 'hspillman2h@skyrock.com', '9 Arapahoe Place');
INSERT INTO Member (id, phone, name, gender, dateOfBirth, email, address) VALUES (91, '8686237818', 'Der Kilban', 'Other', '1985-07-12', 'dkilban2i@cocolog-nifty.com', '43 Northfield Alley');
INSERT INTO Member (id, phone, name, gender, dateOfBirth, email, address) VALUES (92, '2836037361', 'Jerry Dodman', 'Other', '1973-12-27', 'jdodman2j@prlog.org', NULL);
INSERT INTO Member (id, phone, name, gender, dateOfBirth, email, address) VALUES (93, '1824458836', 'Hector Labroue', 'Male', '2007-06-13', 'hlabroue2k@bbc.co.uk', '1 Maryland Lane');
INSERT INTO Member (id, phone, name, gender, dateOfBirth, email, address) VALUES (94, '5138052219', 'Leonanie Hysom', 'Other', '1981-01-18', 'lhysom2l@timesonline.co.uk', '357 Carpenter Drive');
INSERT INTO Member (id, phone, name, gender, dateOfBirth, email, address) VALUES (95, '7501678362', 'Sauveur Load', 'Female', '1993-07-29', 'sload2m@prweb.com', NULL);
INSERT INTO Member (id, phone, name, gender, dateOfBirth, email, address) VALUES (96, '8611714560', 'Maia Starte', 'Female', '1973-11-26', NULL, '34548 2nd Way');
INSERT INTO Member (id, phone, name, gender, dateOfBirth, email, address) VALUES (97, '4401835807', 'Aurel Drought', 'Other', '1971-12-24', 'adrought2o@theatlantic.com', '35937 Holy Cross Alley');
INSERT INTO Member (id, phone, name, gender, dateOfBirth, email, address) VALUES (98, '8934378142', 'Wilie Fibbings', 'Female', '1993-04-19', 'wfibbings2p@indiegogo.com', NULL);
INSERT INTO Member (id, phone, name, gender, dateOfBirth, email, address) VALUES (99, '5152530328', 'Boonie Quinlan', 'Female', '2009-01-12', 'bquinlan2q@nature.com', NULL);
INSERT INTO Member (id, phone, name, gender, dateOfBirth, email, address) VALUES (100, '9245843435', 'Charin Stienham', 'Female', '1993-08-11', 'cstienham2r@cloudflare.com', '4 Graceland Park');
INSERT INTO Member (id, phone, name, gender, dateOfBirth, email, address) VALUES (101, '0000000000', 'Anonymous', 'Other', '0001-01-01 00:00:00', NULL, NULL);


INSERT INTO ImportSheet (id, employeeInChargeId, importDate, totalCost) VALUES (1, 4, '2024-01-01', 0);
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (1, 124, 60, 5.46);
UPDATE BOOK SET maxImportPrice = 5.46 WHERE id = 124;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (1, 146, 22, 8.97);
UPDATE BOOK SET maxImportPrice = 8.97 WHERE id = 146;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (1, 33, 91, 10.16);
UPDATE BOOK SET maxImportPrice = 10.16 WHERE id = 33;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (1, 15, 61, 6.42);
UPDATE BOOK SET maxImportPrice = 6.42 WHERE id = 15;
UPDATE ImportSheet set totalCost = 1841.12 where id = 1;
INSERT INTO ImportSheet (id, employeeInChargeId, importDate, totalCost) VALUES (2, 9, '2024-01-10', 0);
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (2, 67, 68, 7.56);
UPDATE BOOK SET maxImportPrice = 7.56 WHERE id = 67;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (2, 8, 49, 6.12);
UPDATE BOOK SET maxImportPrice = 6.12 WHERE id = 8;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (2, 172, 49, 9.44);
UPDATE BOOK SET maxImportPrice = 9.44 WHERE id = 172;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (2, 104, 45, 8.46);
UPDATE BOOK SET maxImportPrice = 8.46 WHERE id = 104;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (2, 13, 30, 6.71);
UPDATE BOOK SET maxImportPrice = 6.71 WHERE id = 13;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (2, 43, 73, 9.38);
UPDATE BOOK SET maxImportPrice = 9.38 WHERE id = 43;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (2, 36, 25, 5.02);
UPDATE BOOK SET maxImportPrice = 5.02 WHERE id = 36;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (2, 18, 31, 9.24);
UPDATE BOOK SET maxImportPrice = 9.24 WHERE id = 18;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (2, 132, 37, 6.56);
UPDATE BOOK SET maxImportPrice = 6.56 WHERE id = 132;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (2, 49, 38, 8.18);
UPDATE BOOK SET maxImportPrice = 8.18 WHERE id = 49;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (2, 115, 59, 6.42);
UPDATE BOOK SET maxImportPrice = 6.42 WHERE id = 115;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (2, 75, 96, 9.15);
UPDATE BOOK SET maxImportPrice = 9.15 WHERE id = 75;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (2, 90, 72, 9.74);
UPDATE BOOK SET maxImportPrice = 9.74 WHERE id = 90;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (2, 141, 24, 8.66);
UPDATE BOOK SET maxImportPrice = 8.66 WHERE id = 141;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (2, 145, 26, 5.99);
UPDATE BOOK SET maxImportPrice = 5.99 WHERE id = 145;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (2, 55, 54, 7.60);
UPDATE BOOK SET maxImportPrice = 7.60 WHERE id = 55;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (2, 26, 53, 5.48);
UPDATE BOOK SET maxImportPrice = 5.48 WHERE id = 26;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (2, 5, 56, 6.35);
UPDATE BOOK SET maxImportPrice = 6.35 WHERE id = 5;
UPDATE ImportSheet set totalCost = 6887.24 where id = 2;
INSERT INTO ImportSheet (id, employeeInChargeId, importDate, totalCost) VALUES (3, 7, '2024-02-01', 0);
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (3, 70, 84, 8.16);
UPDATE BOOK SET maxImportPrice = 8.16 WHERE id = 70;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (3, 10, 29, 10.43);
UPDATE BOOK SET maxImportPrice = 10.43 WHERE id = 10;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (3, 77, 72, 5.68);
UPDATE BOOK SET maxImportPrice = 5.68 WHERE id = 77;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (3, 148, 75, 9.95);
UPDATE BOOK SET maxImportPrice = 9.95 WHERE id = 148;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (3, 43, 20, 9.88);
UPDATE BOOK SET maxImportPrice = 9.88 WHERE id = 43;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (3, 81, 45, 8.82);
UPDATE BOOK SET maxImportPrice = 8.82 WHERE id = 81;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (3, 143, 50, 8.25);
UPDATE BOOK SET maxImportPrice = 8.25 WHERE id = 143;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (3, 42, 58, 5.66);
UPDATE BOOK SET maxImportPrice = 5.66 WHERE id = 42;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (3, 124, 33, 5.14);
UPDATE BOOK SET maxImportPrice = 5.14 WHERE id = 124;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (3, 125, 82, 8.66);
UPDATE BOOK SET maxImportPrice = 8.66 WHERE id = 125;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (3, 36, 63, 5.28);
UPDATE BOOK SET maxImportPrice = 5.28 WHERE id = 36;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (3, 6, 46, 10.13);
UPDATE BOOK SET maxImportPrice = 10.13 WHERE id = 6;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (3, 158, 65, 10.29);
UPDATE BOOK SET maxImportPrice = 10.29 WHERE id = 158;
UPDATE ImportSheet set totalCost = 5825.61 where id = 3;
INSERT INTO ImportSheet (id, employeeInChargeId, importDate, totalCost) VALUES (4, 7, '2024-03-01', 0);
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (4, 72, 74, 5.90);
UPDATE BOOK SET maxImportPrice = 5.90 WHERE id = 72;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (4, 54, 85, 7.14);
UPDATE BOOK SET maxImportPrice = 7.14 WHERE id = 54;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (4, 22, 90, 7.16);
UPDATE BOOK SET maxImportPrice = 7.16 WHERE id = 22;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (4, 86, 25, 6.23);
UPDATE BOOK SET maxImportPrice = 6.23 WHERE id = 86;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (4, 83, 22, 6.83);
UPDATE BOOK SET maxImportPrice = 6.83 WHERE id = 83;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (4, 139, 43, 7.61);
UPDATE BOOK SET maxImportPrice = 7.61 WHERE id = 139;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (4, 157, 89, 9.24);
UPDATE BOOK SET maxImportPrice = 9.24 WHERE id = 157;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (4, 48, 31, 7.35);
UPDATE BOOK SET maxImportPrice = 7.35 WHERE id = 48;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (4, 97, 50, 9.13);
UPDATE BOOK SET maxImportPrice = 9.13 WHERE id = 97;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (4, 156, 80, 6.87);
UPDATE BOOK SET maxImportPrice = 6.87 WHERE id = 156;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (4, 79, 63, 8.96);
UPDATE BOOK SET maxImportPrice = 8.96 WHERE id = 79;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (4, 138, 62, 9.10);
UPDATE BOOK SET maxImportPrice = 9.10 WHERE id = 138;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (4, 164, 54, 9.22);
UPDATE BOOK SET maxImportPrice = 9.22 WHERE id = 164;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (4, 44, 63, 5.75);
UPDATE BOOK SET maxImportPrice = 5.75 WHERE id = 44;
UPDATE ImportSheet set totalCost = 6366.26 where id = 4;
INSERT INTO ImportSheet (id, employeeInChargeId, importDate, totalCost) VALUES (5, 1, '2024-02-27', 0);
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (5, 2, 96, 8.90);
UPDATE BOOK SET maxImportPrice = 8.90 WHERE id = 2 and 8.90 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 96 WHERE id = 2;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (5, 3, 62, 6.72);
UPDATE BOOK SET maxImportPrice = 6.72 WHERE id = 3 and 6.72 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 62 WHERE id = 3;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (5, 4, 54, 8.09);
UPDATE BOOK SET maxImportPrice = 8.09 WHERE id = 4 and 8.09 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 54 WHERE id = 4;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (5, 5, 39, 6.57);
UPDATE BOOK SET maxImportPrice = 6.57 WHERE id = 5 and 6.57 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 39 WHERE id = 5;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (5, 6, 72, 10.13);
UPDATE BOOK SET maxImportPrice = 10.13 WHERE id = 6 and 10.13 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 72 WHERE id = 6;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (5, 7, 24, 8.84);
UPDATE BOOK SET maxImportPrice = 8.84 WHERE id = 7 and 8.84 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 24 WHERE id = 7;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (5, 8, 39, 6.02);
UPDATE BOOK SET maxImportPrice = 6.02 WHERE id = 8 and 6.02 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 39 WHERE id = 8;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (5, 9, 99, 7.46);
UPDATE BOOK SET maxImportPrice = 7.46 WHERE id = 9 and 7.46 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 99 WHERE id = 9;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (5, 10, 94, 10.34);
UPDATE BOOK SET maxImportPrice = 10.34 WHERE id = 10 and 10.34 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 94 WHERE id = 10;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (5, 11, 93, 8.88);
UPDATE BOOK SET maxImportPrice = 8.88 WHERE id = 11 and 8.88 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 93 WHERE id = 11;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (5, 12, 27, 10.28);
UPDATE BOOK SET maxImportPrice = 10.28 WHERE id = 12 and 10.28 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 27 WHERE id = 12;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (5, 13, 31, 6.54);
UPDATE BOOK SET maxImportPrice = 6.54 WHERE id = 13 and 6.54 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 31 WHERE id = 13;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (5, 14, 62, 6.09);
UPDATE BOOK SET maxImportPrice = 6.09 WHERE id = 14 and 6.09 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 62 WHERE id = 14;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (5, 15, 78, 6.30);
UPDATE BOOK SET maxImportPrice = 6.30 WHERE id = 15 and 6.30 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 78 WHERE id = 15;
UPDATE ImportSheet set totalCost = 7026.05 where id = 5;
INSERT INTO ImportSheet (id, employeeInChargeId, importDate, totalCost) VALUES (6, 8, '2024-02-19', 0);
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (6, 16, 33, 8.83);
UPDATE BOOK SET maxImportPrice = 8.83 WHERE id = 16 and 8.83 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 33 WHERE id = 16;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (6, 17, 60, 8.83);
UPDATE BOOK SET maxImportPrice = 8.83 WHERE id = 17 and 8.83 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 60 WHERE id = 17;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (6, 18, 54, 9.08);
UPDATE BOOK SET maxImportPrice = 9.08 WHERE id = 18 and 9.08 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 54 WHERE id = 18;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (6, 19, 32, 9.42);
UPDATE BOOK SET maxImportPrice = 9.42 WHERE id = 19 and 9.42 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 32 WHERE id = 19;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (6, 20, 48, 5.06);
UPDATE BOOK SET maxImportPrice = 5.06 WHERE id = 20 and 5.06 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 48 WHERE id = 20;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (6, 21, 45, 5.13);
UPDATE BOOK SET maxImportPrice = 5.13 WHERE id = 21 and 5.13 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 45 WHERE id = 21;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (6, 22, 62, 7.22);
UPDATE BOOK SET maxImportPrice = 7.22 WHERE id = 22 and 7.22 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 62 WHERE id = 22;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (6, 23, 93, 7.78);
UPDATE BOOK SET maxImportPrice = 7.78 WHERE id = 23 and 7.78 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 93 WHERE id = 23;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (6, 24, 57, 6.84);
UPDATE BOOK SET maxImportPrice = 6.84 WHERE id = 24 and 6.84 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 57 WHERE id = 24;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (6, 25, 95, 7.02);
UPDATE BOOK SET maxImportPrice = 7.02 WHERE id = 25 and 7.02 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 95 WHERE id = 25;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (6, 26, 51, 5.63);
UPDATE BOOK SET maxImportPrice = 5.63 WHERE id = 26 and 5.63 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 51 WHERE id = 26;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (6, 27, 41, 8.65);
UPDATE BOOK SET maxImportPrice = 8.65 WHERE id = 27 and 8.65 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 41 WHERE id = 27;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (6, 28, 82, 8.90);
UPDATE BOOK SET maxImportPrice = 8.90 WHERE id = 28 and 8.90 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 82 WHERE id = 28;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (6, 29, 65, 9.47);
UPDATE BOOK SET maxImportPrice = 9.47 WHERE id = 29 and 9.47 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 65 WHERE id = 29;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (6, 30, 97, 5.52);
UPDATE BOOK SET maxImportPrice = 5.52 WHERE id = 30 and 5.52 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 97 WHERE id = 30;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (6, 31, 93, 5.30);
UPDATE BOOK SET maxImportPrice = 5.30 WHERE id = 31 and 5.30 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 93 WHERE id = 31;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (6, 32, 21, 6.94);
UPDATE BOOK SET maxImportPrice = 6.94 WHERE id = 32 and 6.94 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 21 WHERE id = 32;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (6, 33, 61, 9.73);
UPDATE BOOK SET maxImportPrice = 9.73 WHERE id = 33 and 9.73 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 61 WHERE id = 33;
UPDATE ImportSheet set totalCost = 8069.38 where id = 6;
INSERT INTO ImportSheet (id, employeeInChargeId, importDate, totalCost) VALUES (7, 6, '2024-02-15', 0);
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (7, 34, 49, 7.89);
UPDATE BOOK SET maxImportPrice = 7.89 WHERE id = 34 and 7.89 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 49 WHERE id = 34;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (7, 35, 58, 6.21);
UPDATE BOOK SET maxImportPrice = 6.21 WHERE id = 35 and 6.21 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 58 WHERE id = 35;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (7, 36, 80, 5.19);
UPDATE BOOK SET maxImportPrice = 5.19 WHERE id = 36 and 5.19 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 80 WHERE id = 36;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (7, 37, 97, 6.03);
UPDATE BOOK SET maxImportPrice = 6.03 WHERE id = 37 and 6.03 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 97 WHERE id = 37;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (7, 38, 89, 5.38);
UPDATE BOOK SET maxImportPrice = 5.38 WHERE id = 38 and 5.38 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 89 WHERE id = 38;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (7, 39, 26, 8.97);
UPDATE BOOK SET maxImportPrice = 8.97 WHERE id = 39 and 8.97 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 26 WHERE id = 39;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (7, 40, 33, 6.58);
UPDATE BOOK SET maxImportPrice = 6.58 WHERE id = 40 and 6.58 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 33 WHERE id = 40;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (7, 41, 20, 7.47);
UPDATE BOOK SET maxImportPrice = 7.47 WHERE id = 41 and 7.47 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 20 WHERE id = 41;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (7, 42, 60, 5.71);
UPDATE BOOK SET maxImportPrice = 5.71 WHERE id = 42 and 5.71 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 60 WHERE id = 42;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (7, 43, 99, 10.15);
UPDATE BOOK SET maxImportPrice = 10.15 WHERE id = 43 and 10.15 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 99 WHERE id = 43;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (7, 44, 70, 5.50);
UPDATE BOOK SET maxImportPrice = 5.50 WHERE id = 44 and 5.50 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 70 WHERE id = 44;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (7, 45, 42, 5.72);
UPDATE BOOK SET maxImportPrice = 5.72 WHERE id = 45 and 5.72 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 42 WHERE id = 45;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (7, 46, 58, 8.66);
UPDATE BOOK SET maxImportPrice = 8.66 WHERE id = 46 and 8.66 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 58 WHERE id = 46;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (7, 47, 26, 10.56);
UPDATE BOOK SET maxImportPrice = 10.56 WHERE id = 47 and 10.56 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 26 WHERE id = 47;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (7, 48, 54, 7.16);
UPDATE BOOK SET maxImportPrice = 7.16 WHERE id = 48 and 7.16 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 54 WHERE id = 48;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (7, 49, 48, 8.32);
UPDATE BOOK SET maxImportPrice = 8.32 WHERE id = 49 and 8.32 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 48 WHERE id = 49;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (7, 50, 74, 5.55);
UPDATE BOOK SET maxImportPrice = 5.55 WHERE id = 50 and 5.55 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 74 WHERE id = 50;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (7, 51, 63, 5.45);
UPDATE BOOK SET maxImportPrice = 5.45 WHERE id = 51 and 5.45 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 63 WHERE id = 51;
UPDATE ImportSheet set totalCost = 7115.06 where id = 7;
INSERT INTO ImportSheet (id, employeeInChargeId, importDate, totalCost) VALUES (8, 1, '2024-02-22', 0);
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (8, 52, 95, 5.99);
UPDATE BOOK SET maxImportPrice = 5.99 WHERE id = 52 and 5.99 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 95 WHERE id = 52;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (8, 53, 80, 7.09);
UPDATE BOOK SET maxImportPrice = 7.09 WHERE id = 53 and 7.09 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 80 WHERE id = 53;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (8, 54, 92, 7.20);
UPDATE BOOK SET maxImportPrice = 7.20 WHERE id = 54 and 7.20 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 92 WHERE id = 54;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (8, 55, 90, 7.74);
UPDATE BOOK SET maxImportPrice = 7.74 WHERE id = 55 and 7.74 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 90 WHERE id = 55;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (8, 56, 40, 7.73);
UPDATE BOOK SET maxImportPrice = 7.73 WHERE id = 56 and 7.73 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 40 WHERE id = 56;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (8, 57, 82, 9.22);
UPDATE BOOK SET maxImportPrice = 9.22 WHERE id = 57 and 9.22 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 82 WHERE id = 57;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (8, 58, 82, 9.76);
UPDATE BOOK SET maxImportPrice = 9.76 WHERE id = 58 and 9.76 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 82 WHERE id = 58;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (8, 59, 84, 6.62);
UPDATE BOOK SET maxImportPrice = 6.62 WHERE id = 59 and 6.62 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 84 WHERE id = 59;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (8, 60, 64, 8.75);
UPDATE BOOK SET maxImportPrice = 8.75 WHERE id = 60 and 8.75 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 64 WHERE id = 60;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (8, 61, 100, 7.45);
UPDATE BOOK SET maxImportPrice = 7.45 WHERE id = 61 and 7.45 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 100 WHERE id = 61;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (8, 62, 97, 7.38);
UPDATE BOOK SET maxImportPrice = 7.38 WHERE id = 62 and 7.38 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 97 WHERE id = 62;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (8, 63, 92, 6.55);
UPDATE BOOK SET maxImportPrice = 6.55 WHERE id = 63 and 6.55 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 92 WHERE id = 63;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (8, 64, 46, 9.30);
UPDATE BOOK SET maxImportPrice = 9.30 WHERE id = 64 and 9.30 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 46 WHERE id = 64;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (8, 65, 93, 10.33);
UPDATE BOOK SET maxImportPrice = 10.33 WHERE id = 65 and 10.33 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 93 WHERE id = 65;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (8, 66, 62, 7.01);
UPDATE BOOK SET maxImportPrice = 7.01 WHERE id = 66 and 7.01 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 62 WHERE id = 66;
UPDATE ImportSheet set totalCost = 9774.16 where id = 8;
INSERT INTO ImportSheet (id, employeeInChargeId, importDate, totalCost) VALUES (9, 6, '2024-03-10', 0);
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (9, 67, 73, 7.12);
UPDATE BOOK SET maxImportPrice = 7.12 WHERE id = 67 and 7.12 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 73 WHERE id = 67;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (9, 68, 37, 6.99);
UPDATE BOOK SET maxImportPrice = 6.99 WHERE id = 68 and 6.99 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 37 WHERE id = 68;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (9, 69, 94, 9.44);
UPDATE BOOK SET maxImportPrice = 9.44 WHERE id = 69 and 9.44 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 94 WHERE id = 69;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (9, 70, 45, 8.30);
UPDATE BOOK SET maxImportPrice = 8.30 WHERE id = 70 and 8.30 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 45 WHERE id = 70;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (9, 71, 59, 8.94);
UPDATE BOOK SET maxImportPrice = 8.94 WHERE id = 71 and 8.94 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 59 WHERE id = 71;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (9, 72, 94, 6.05);
UPDATE BOOK SET maxImportPrice = 6.05 WHERE id = 72 and 6.05 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 94 WHERE id = 72;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (9, 73, 85, 7.15);
UPDATE BOOK SET maxImportPrice = 7.15 WHERE id = 73 and 7.15 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 85 WHERE id = 73;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (9, 74, 70, 10.08);
UPDATE BOOK SET maxImportPrice = 10.08 WHERE id = 74 and 10.08 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 70 WHERE id = 74;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (9, 75, 60, 9.65);
UPDATE BOOK SET maxImportPrice = 9.65 WHERE id = 75 and 9.65 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 60 WHERE id = 75;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (9, 76, 97, 6.59);
UPDATE BOOK SET maxImportPrice = 6.59 WHERE id = 76 and 6.59 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 97 WHERE id = 76;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (9, 77, 83, 5.49);
UPDATE BOOK SET maxImportPrice = 5.49 WHERE id = 77 and 5.49 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 83 WHERE id = 77;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (9, 78, 93, 10.33);
UPDATE BOOK SET maxImportPrice = 10.33 WHERE id = 78 and 10.33 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 93 WHERE id = 78;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (9, 79, 88, 8.65);
UPDATE BOOK SET maxImportPrice = 8.65 WHERE id = 79 and 8.65 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 88 WHERE id = 79;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (9, 80, 30, 8.39);
UPDATE BOOK SET maxImportPrice = 8.39 WHERE id = 80 and 8.39 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 30 WHERE id = 80;
UPDATE ImportSheet set totalCost = 8421.47 where id = 9;
INSERT INTO ImportSheet (id, employeeInChargeId, importDate, totalCost) VALUES (10, 11, '2024-04-23', 0);
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (10, 81, 36, 8.90);
UPDATE BOOK SET maxImportPrice = 8.90 WHERE id = 81 and 8.90 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 36 WHERE id = 81;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (10, 82, 62, 5.50);
UPDATE BOOK SET maxImportPrice = 5.50 WHERE id = 82 and 5.50 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 62 WHERE id = 82;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (10, 83, 56, 7.07);
UPDATE BOOK SET maxImportPrice = 7.07 WHERE id = 83 and 7.07 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 56 WHERE id = 83;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (10, 84, 77, 10.50);
UPDATE BOOK SET maxImportPrice = 10.50 WHERE id = 84 and 10.50 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 77 WHERE id = 84;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (10, 85, 33, 8.91);
UPDATE BOOK SET maxImportPrice = 8.91 WHERE id = 85 and 8.91 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 33 WHERE id = 85;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (10, 86, 43, 6.13);
UPDATE BOOK SET maxImportPrice = 6.13 WHERE id = 86 and 6.13 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 43 WHERE id = 86;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (10, 87, 85, 7.65);
UPDATE BOOK SET maxImportPrice = 7.65 WHERE id = 87 and 7.65 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 85 WHERE id = 87;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (10, 88, 77, 5.21);
UPDATE BOOK SET maxImportPrice = 5.21 WHERE id = 88 and 5.21 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 77 WHERE id = 88;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (10, 89, 22, 7.89);
UPDATE BOOK SET maxImportPrice = 7.89 WHERE id = 89 and 7.89 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 22 WHERE id = 89;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (10, 90, 46, 9.91);
UPDATE BOOK SET maxImportPrice = 9.91 WHERE id = 90 and 9.91 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 46 WHERE id = 90;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (10, 91, 75, 9.41);
UPDATE BOOK SET maxImportPrice = 9.41 WHERE id = 91 and 9.41 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 75 WHERE id = 91;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (10, 92, 27, 7.42);
UPDATE BOOK SET maxImportPrice = 7.42 WHERE id = 92 and 7.42 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 27 WHERE id = 92;
UPDATE ImportSheet set totalCost = 5010.39 where id = 10;
INSERT INTO ImportSheet (id, employeeInChargeId, importDate, totalCost) VALUES (11, 1, '2024-04-16', 0);
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (11, 93, 41, 6.14);
UPDATE BOOK SET maxImportPrice = 6.14 WHERE id = 93 and 6.14 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 41 WHERE id = 93;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (11, 94, 72, 10.09);
UPDATE BOOK SET maxImportPrice = 10.09 WHERE id = 94 and 10.09 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 72 WHERE id = 94;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (11, 95, 30, 6.65);
UPDATE BOOK SET maxImportPrice = 6.65 WHERE id = 95 and 6.65 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 30 WHERE id = 95;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (11, 96, 88, 7.05);
UPDATE BOOK SET maxImportPrice = 7.05 WHERE id = 96 and 7.05 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 88 WHERE id = 96;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (11, 97, 82, 7.67);
UPDATE BOOK SET maxImportPrice = 7.67 WHERE id = 97 and 7.67 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 82 WHERE id = 97;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (11, 98, 93, 7.40);
UPDATE BOOK SET maxImportPrice = 7.40 WHERE id = 98 and 7.40 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 93 WHERE id = 98;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (11, 99, 64, 6.41);
UPDATE BOOK SET maxImportPrice = 6.41 WHERE id = 99 and 6.41 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 64 WHERE id = 99;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (11, 100, 96, 8.71);
UPDATE BOOK SET maxImportPrice = 8.71 WHERE id = 100 and 8.71 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 96 WHERE id = 100;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (11, 101, 32, 6.77);
UPDATE BOOK SET maxImportPrice = 6.77 WHERE id = 101 and 6.77 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 32 WHERE id = 101;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (11, 102, 23, 5.79);
UPDATE BOOK SET maxImportPrice = 5.79 WHERE id = 102 and 5.79 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 23 WHERE id = 102;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (11, 103, 20, 8.76);
UPDATE BOOK SET maxImportPrice = 8.76 WHERE id = 103 and 8.76 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 20 WHERE id = 103;
UPDATE ImportSheet set totalCost = 4886.67 where id = 11;
INSERT INTO ImportSheet (id, employeeInChargeId, importDate, totalCost) VALUES (12, 10, '2024-04-08', 0);
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (12, 104, 56, 8.17);
UPDATE BOOK SET maxImportPrice = 8.17 WHERE id = 104 and 8.17 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 56 WHERE id = 104;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (12, 105, 33, 8.84);
UPDATE BOOK SET maxImportPrice = 8.84 WHERE id = 105 and 8.84 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 33 WHERE id = 105;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (12, 106, 34, 8.89);
UPDATE BOOK SET maxImportPrice = 8.89 WHERE id = 106 and 8.89 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 34 WHERE id = 106;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (12, 107, 69, 7.27);
UPDATE BOOK SET maxImportPrice = 7.27 WHERE id = 107 and 7.27 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 69 WHERE id = 107;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (12, 108, 48, 7.14);
UPDATE BOOK SET maxImportPrice = 7.14 WHERE id = 108 and 7.14 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 48 WHERE id = 108;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (12, 109, 73, 10.17);
UPDATE BOOK SET maxImportPrice = 10.17 WHERE id = 109 and 10.17 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 73 WHERE id = 109;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (12, 110, 25, 8.17);
UPDATE BOOK SET maxImportPrice = 8.17 WHERE id = 110 and 8.17 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 25 WHERE id = 110;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (12, 111, 69, 8.99);
UPDATE BOOK SET maxImportPrice = 8.99 WHERE id = 111 and 8.99 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 69 WHERE id = 111;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (12, 112, 35, 7.45);
UPDATE BOOK SET maxImportPrice = 7.45 WHERE id = 112 and 7.45 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 35 WHERE id = 112;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (12, 113, 100, 6.07);
UPDATE BOOK SET maxImportPrice = 6.07 WHERE id = 113 and 6.07 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 100 WHERE id = 113;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (12, 114, 75, 7.57);
UPDATE BOOK SET maxImportPrice = 7.57 WHERE id = 114 and 7.57 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 75 WHERE id = 114;
UPDATE ImportSheet set totalCost = 4898.32 where id = 12;
INSERT INTO ImportSheet (id, employeeInChargeId, importDate, totalCost) VALUES (13, 1, '2024-04-28', 0);
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (13, 115, 23, 6.59);
UPDATE BOOK SET maxImportPrice = 6.59 WHERE id = 115 and 6.59 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 23 WHERE id = 115;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (13, 116, 95, 9.94);
UPDATE BOOK SET maxImportPrice = 9.94 WHERE id = 116 and 9.94 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 95 WHERE id = 116;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (13, 117, 71, 5.08);
UPDATE BOOK SET maxImportPrice = 5.08 WHERE id = 117 and 5.08 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 71 WHERE id = 117;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (13, 118, 71, 9.78);
UPDATE BOOK SET maxImportPrice = 9.78 WHERE id = 118 and 9.78 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 71 WHERE id = 118;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (13, 119, 61, 8.69);
UPDATE BOOK SET maxImportPrice = 8.69 WHERE id = 119 and 8.69 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 61 WHERE id = 119;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (13, 120, 21, 6.68);
UPDATE BOOK SET maxImportPrice = 6.68 WHERE id = 120 and 6.68 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 21 WHERE id = 120;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (13, 121, 23, 7.01);
UPDATE BOOK SET maxImportPrice = 7.01 WHERE id = 121 and 7.01 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 23 WHERE id = 121;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (13, 122, 42, 7.84);
UPDATE BOOK SET maxImportPrice = 7.84 WHERE id = 122 and 7.84 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 42 WHERE id = 122;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (13, 123, 37, 9.93);
UPDATE BOOK SET maxImportPrice = 9.93 WHERE id = 123 and 9.93 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 37 WHERE id = 123;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (13, 124, 39, 5.37);
UPDATE BOOK SET maxImportPrice = 5.37 WHERE id = 124 and 5.37 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 39 WHERE id = 124;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (13, 125, 29, 8.29);
UPDATE BOOK SET maxImportPrice = 8.29 WHERE id = 125 and 8.29 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 29 WHERE id = 125;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (13, 126, 47, 6.84);
UPDATE BOOK SET maxImportPrice = 6.84 WHERE id = 126 and 6.84 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 47 WHERE id = 126;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (13, 127, 100, 9.81);
UPDATE BOOK SET maxImportPrice = 9.81 WHERE id = 127 and 9.81 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 100 WHERE id = 127;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (13, 128, 69, 9.09);
UPDATE BOOK SET maxImportPrice = 9.09 WHERE id = 128 and 9.09 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 69 WHERE id = 128;
UPDATE ImportSheet set totalCost = 6058.75 where id = 13;
INSERT INTO ImportSheet (id, employeeInChargeId, importDate, totalCost) VALUES (14, 6, '2024-03-02', 0);
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (14, 129, 75, 9.94);
UPDATE BOOK SET maxImportPrice = 9.94 WHERE id = 129 and 9.94 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 75 WHERE id = 129;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (14, 130, 35, 6.42);
UPDATE BOOK SET maxImportPrice = 6.42 WHERE id = 130 and 6.42 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 35 WHERE id = 130;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (14, 131, 57, 8.98);
UPDATE BOOK SET maxImportPrice = 8.98 WHERE id = 131 and 8.98 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 57 WHERE id = 131;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (14, 132, 71, 6.18);
UPDATE BOOK SET maxImportPrice = 6.18 WHERE id = 132 and 6.18 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 71 WHERE id = 132;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (14, 133, 71, 8.96);
UPDATE BOOK SET maxImportPrice = 8.96 WHERE id = 133 and 8.96 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 71 WHERE id = 133;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (14, 134, 62, 5.63);
UPDATE BOOK SET maxImportPrice = 5.63 WHERE id = 134 and 5.63 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 62 WHERE id = 134;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (14, 135, 59, 6.82);
UPDATE BOOK SET maxImportPrice = 6.82 WHERE id = 135 and 6.82 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 59 WHERE id = 135;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (14, 136, 47, 7.59);
UPDATE BOOK SET maxImportPrice = 7.59 WHERE id = 136 and 7.59 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 47 WHERE id = 136;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (14, 137, 80, 7.42);
UPDATE BOOK SET maxImportPrice = 7.42 WHERE id = 137 and 7.42 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 80 WHERE id = 137;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (14, 138, 66, 9.58);
UPDATE BOOK SET maxImportPrice = 9.58 WHERE id = 138 and 9.58 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 66 WHERE id = 138;
UPDATE ImportSheet set totalCost = 4891.05 where id = 14;
INSERT INTO ImportSheet (id, employeeInChargeId, importDate, totalCost) VALUES (15, 6, '2024-02-21', 0);
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (15, 139, 32, 7.54);
UPDATE BOOK SET maxImportPrice = 7.54 WHERE id = 139 and 7.54 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 32 WHERE id = 139;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (15, 140, 47, 6.91);
UPDATE BOOK SET maxImportPrice = 6.91 WHERE id = 140 and 6.91 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 47 WHERE id = 140;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (15, 141, 65, 8.97);
UPDATE BOOK SET maxImportPrice = 8.97 WHERE id = 141 and 8.97 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 65 WHERE id = 141;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (15, 142, 56, 10.21);
UPDATE BOOK SET maxImportPrice = 10.21 WHERE id = 142 and 10.21 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 56 WHERE id = 142;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (15, 143, 93, 8.47);
UPDATE BOOK SET maxImportPrice = 8.47 WHERE id = 143 and 8.47 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 93 WHERE id = 143;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (15, 144, 65, 6.37);
UPDATE BOOK SET maxImportPrice = 6.37 WHERE id = 144 and 6.37 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 65 WHERE id = 144;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (15, 145, 57, 6.26);
UPDATE BOOK SET maxImportPrice = 6.26 WHERE id = 145 and 6.26 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 57 WHERE id = 145;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (15, 146, 75, 8.67);
UPDATE BOOK SET maxImportPrice = 8.67 WHERE id = 146 and 8.67 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 75 WHERE id = 146;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (15, 147, 84, 7.26);
UPDATE BOOK SET maxImportPrice = 7.26 WHERE id = 147 and 7.26 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 84 WHERE id = 147;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (15, 148, 96, 9.52);
UPDATE BOOK SET maxImportPrice = 9.52 WHERE id = 148 and 9.52 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 96 WHERE id = 148;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (15, 149, 48, 9.72);
UPDATE BOOK SET maxImportPrice = 9.72 WHERE id = 149 and 9.72 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 48 WHERE id = 149;
UPDATE ImportSheet set totalCost = 5920.01 where id = 15;
INSERT INTO ImportSheet (id, employeeInChargeId, importDate, totalCost) VALUES (16, 4, '2024-03-10', 0);
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (16, 150, 54, 7.84);
UPDATE BOOK SET maxImportPrice = 7.84 WHERE id = 150 and 7.84 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 54 WHERE id = 150;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (16, 151, 25, 9.79);
UPDATE BOOK SET maxImportPrice = 9.79 WHERE id = 151 and 9.79 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 25 WHERE id = 151;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (16, 152, 96, 6.96);
UPDATE BOOK SET maxImportPrice = 6.96 WHERE id = 152 and 6.96 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 96 WHERE id = 152;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (16, 153, 86, 8.07);
UPDATE BOOK SET maxImportPrice = 8.07 WHERE id = 153 and 8.07 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 86 WHERE id = 153;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (16, 154, 23, 8.08);
UPDATE BOOK SET maxImportPrice = 8.08 WHERE id = 154 and 8.08 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 23 WHERE id = 154;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (16, 155, 46, 9.36);
UPDATE BOOK SET maxImportPrice = 9.36 WHERE id = 155 and 9.36 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 46 WHERE id = 155;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (16, 156, 87, 7.12);
UPDATE BOOK SET maxImportPrice = 7.12 WHERE id = 156 and 7.12 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 87 WHERE id = 156;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (16, 157, 38, 9.99);
UPDATE BOOK SET maxImportPrice = 9.99 WHERE id = 157 and 9.99 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 38 WHERE id = 157;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (16, 158, 29, 10.85);
UPDATE BOOK SET maxImportPrice = 10.85 WHERE id = 158 and 10.85 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 29 WHERE id = 158;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (16, 159, 67, 9.97);
UPDATE BOOK SET maxImportPrice = 9.97 WHERE id = 159 and 9.97 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 67 WHERE id = 159;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (16, 160, 47, 8.08);
UPDATE BOOK SET maxImportPrice = 8.08 WHERE id = 160 and 8.08 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 47 WHERE id = 160;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (16, 161, 41, 7.41);
UPDATE BOOK SET maxImportPrice = 7.41 WHERE id = 161 and 7.41 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 41 WHERE id = 161;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (16, 162, 56, 7.23);
UPDATE BOOK SET maxImportPrice = 7.23 WHERE id = 162 and 7.23 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 56 WHERE id = 162;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (16, 163, 54, 7.45);
UPDATE BOOK SET maxImportPrice = 7.45 WHERE id = 163 and 7.45 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 54 WHERE id = 163;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (16, 164, 77, 8.67);
UPDATE BOOK SET maxImportPrice = 8.67 WHERE id = 164 and 8.67 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 77 WHERE id = 164;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (16, 165, 26, 6.10);
UPDATE BOOK SET maxImportPrice = 6.10 WHERE id = 165 and 6.10 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 26 WHERE id = 165;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (16, 166, 55, 8.14);
UPDATE BOOK SET maxImportPrice = 8.14 WHERE id = 166 and 8.14 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 55 WHERE id = 166;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (16, 167, 76, 7.96);
UPDATE BOOK SET maxImportPrice = 7.96 WHERE id = 167 and 7.96 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 76 WHERE id = 167;
UPDATE ImportSheet set totalCost = 7997.99 where id = 16;
INSERT INTO ImportSheet (id, employeeInChargeId, importDate, totalCost) VALUES (17, 9, '2024-03-14', 0);
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (17, 168, 58, 8.21);
UPDATE BOOK SET maxImportPrice = 8.21 WHERE id = 168 and 8.21 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 58 WHERE id = 168;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (17, 169, 97, 8.50);
UPDATE BOOK SET maxImportPrice = 8.50 WHERE id = 169 and 8.50 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 97 WHERE id = 169;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (17, 170, 41, 8.26);
UPDATE BOOK SET maxImportPrice = 8.26 WHERE id = 170 and 8.26 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 41 WHERE id = 170;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (17, 171, 71, 10.46);
UPDATE BOOK SET maxImportPrice = 10.46 WHERE id = 171 and 10.46 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 71 WHERE id = 171;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (17, 172, 87, 9.61);
UPDATE BOOK SET maxImportPrice = 9.61 WHERE id = 172 and 9.61 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 87 WHERE id = 172;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (17, 173, 27, 5.51);
UPDATE BOOK SET maxImportPrice = 5.51 WHERE id = 173 and 5.51 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 27 WHERE id = 173;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (17, 174, 91, 6.55);
UPDATE BOOK SET maxImportPrice = 6.55 WHERE id = 174 and 6.55 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 91 WHERE id = 174;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (17, 175, 58, 7.03);
UPDATE BOOK SET maxImportPrice = 7.03 WHERE id = 175 and 7.03 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 58 WHERE id = 175;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (17, 176, 29, 9.08);
UPDATE BOOK SET maxImportPrice = 9.08 WHERE id = 176 and 9.08 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 29 WHERE id = 176;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (17, 177, 61, 7.97);
UPDATE BOOK SET maxImportPrice = 7.97 WHERE id = 177 and 7.97 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 61 WHERE id = 177;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (17, 178, 41, 8.88);
UPDATE BOOK SET maxImportPrice = 8.88 WHERE id = 178 and 8.88 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 41 WHERE id = 178;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (17, 30, 100, 5.56);
UPDATE BOOK SET maxImportPrice = 5.56 WHERE id = 30 and 5.56 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 100 WHERE id = 30;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (17, 124, 21, 5.14);
UPDATE BOOK SET maxImportPrice = 5.14 WHERE id = 124 and 5.14 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 21 WHERE id = 124;
UPDATE ImportSheet set totalCost = 6148.14 where id = 17;
INSERT INTO ImportSheet (id, employeeInChargeId, importDate, totalCost) VALUES (18, 1, '2024-03-14', 0);
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (18, 152, 42, 7.33);
UPDATE BOOK SET maxImportPrice = 7.33 WHERE id = 152 and 7.33 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 42 WHERE id = 152;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (18, 90, 95, 9.50);
UPDATE BOOK SET maxImportPrice = 9.50 WHERE id = 90 and 9.50 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 95 WHERE id = 90;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (18, 147, 68, 6.77);
UPDATE BOOK SET maxImportPrice = 6.77 WHERE id = 147 and 6.77 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 68 WHERE id = 147;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (18, 119, 22, 8.92);
UPDATE BOOK SET maxImportPrice = 8.92 WHERE id = 119 and 8.92 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 22 WHERE id = 119;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (18, 62, 71, 7.44);
UPDATE BOOK SET maxImportPrice = 7.44 WHERE id = 62 and 7.44 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 71 WHERE id = 62;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (18, 158, 86, 10.85);
UPDATE BOOK SET maxImportPrice = 10.85 WHERE id = 158 and 10.85 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 86 WHERE id = 158;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (18, 55, 85, 7.34);
UPDATE BOOK SET maxImportPrice = 7.34 WHERE id = 55 and 7.34 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 85 WHERE id = 55;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (18, 87, 73, 7.39);
UPDATE BOOK SET maxImportPrice = 7.39 WHERE id = 87 and 7.39 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 73 WHERE id = 87;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (18, 65, 90, 9.71);
UPDATE BOOK SET maxImportPrice = 9.71 WHERE id = 65 and 9.71 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 90 WHERE id = 65;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (18, 176, 51, 8.54);
UPDATE BOOK SET maxImportPrice = 8.54 WHERE id = 176 and 8.54 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 51 WHERE id = 176;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (18, 174, 95, 6.55);
UPDATE BOOK SET maxImportPrice = 6.55 WHERE id = 174 and 6.55 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 95 WHERE id = 174;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (18, 109, 83, 9.74);
UPDATE BOOK SET maxImportPrice = 9.74 WHERE id = 109 and 9.74 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 83 WHERE id = 109;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (18, 96, 36, 7.56);
UPDATE BOOK SET maxImportPrice = 7.56 WHERE id = 96 and 7.56 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 36 WHERE id = 96;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (18, 102, 52, 5.79);
UPDATE BOOK SET maxImportPrice = 5.79 WHERE id = 102 and 5.79 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 52 WHERE id = 102;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (18, 70, 46, 8.82);
UPDATE BOOK SET maxImportPrice = 8.82 WHERE id = 70 and 8.82 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 46 WHERE id = 70;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (18, 156, 44, 6.70);
UPDATE BOOK SET maxImportPrice = 6.70 WHERE id = 156 and 6.70 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 44 WHERE id = 156;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (18, 18, 79, 9.32);
UPDATE BOOK SET maxImportPrice = 9.32 WHERE id = 18 and 9.32 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 79 WHERE id = 18;
UPDATE ImportSheet set totalCost = 9241.82 where id = 18;
INSERT INTO ImportSheet (id, employeeInChargeId, importDate, totalCost) VALUES (19, 5, '2024-03-27', 0);
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (19, 125, 83, 8.81);
UPDATE BOOK SET maxImportPrice = 8.81 WHERE id = 125 and 8.81 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 83 WHERE id = 125;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (19, 20, 72, 5.10);
UPDATE BOOK SET maxImportPrice = 5.10 WHERE id = 20 and 5.10 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 72 WHERE id = 20;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (19, 141, 93, 9.05);
UPDATE BOOK SET maxImportPrice = 9.05 WHERE id = 141 and 9.05 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 93 WHERE id = 141;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (19, 172, 89, 9.36);
UPDATE BOOK SET maxImportPrice = 9.36 WHERE id = 172 and 9.36 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 89 WHERE id = 172;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (19, 89, 99, 7.89);
UPDATE BOOK SET maxImportPrice = 7.89 WHERE id = 89 and 7.89 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 99 WHERE id = 89;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (19, 160, 68, 8.43);
UPDATE BOOK SET maxImportPrice = 8.43 WHERE id = 160 and 8.43 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 68 WHERE id = 160;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (19, 151, 40, 9.96);
UPDATE BOOK SET maxImportPrice = 9.96 WHERE id = 151 and 9.96 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 40 WHERE id = 151;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (19, 177, 83, 7.77);
UPDATE BOOK SET maxImportPrice = 7.77 WHERE id = 177 and 7.77 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 83 WHERE id = 177;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (19, 150, 66, 7.51);
UPDATE BOOK SET maxImportPrice = 7.51 WHERE id = 150 and 7.51 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 66 WHERE id = 150;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (19, 30, 88, 5.81);
UPDATE BOOK SET maxImportPrice = 5.81 WHERE id = 30 and 5.81 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 88 WHERE id = 30;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (19, 3, 46, 7.08);
UPDATE BOOK SET maxImportPrice = 7.08 WHERE id = 3 and 7.08 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 46 WHERE id = 3;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (19, 82, 34, 5.75);
UPDATE BOOK SET maxImportPrice = 5.75 WHERE id = 82 and 5.75 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 34 WHERE id = 82;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (19, 118, 56, 9.70);
UPDATE BOOK SET maxImportPrice = 9.70 WHERE id = 118 and 9.70 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 56 WHERE id = 118;
UPDATE ImportSheet set totalCost = 7242.1 where id = 19;
INSERT INTO ImportSheet (id, employeeInChargeId, importDate, totalCost) VALUES (20, 2, '2024-03-26', 0);
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (20, 86, 46, 6.13);
UPDATE BOOK SET maxImportPrice = 6.13 WHERE id = 86 and 6.13 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 46 WHERE id = 86;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (20, 14, 86, 6.65);
UPDATE BOOK SET maxImportPrice = 6.65 WHERE id = 14 and 6.65 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 86 WHERE id = 14;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (20, 39, 69, 8.89);
UPDATE BOOK SET maxImportPrice = 8.89 WHERE id = 39 and 8.89 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 69 WHERE id = 39;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (20, 28, 95, 8.83);
UPDATE BOOK SET maxImportPrice = 8.83 WHERE id = 28 and 8.83 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 95 WHERE id = 28;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (20, 10, 29, 10.72);
UPDATE BOOK SET maxImportPrice = 10.72 WHERE id = 10 and 10.72 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 29 WHERE id = 10;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (20, 175, 33, 6.67);
UPDATE BOOK SET maxImportPrice = 6.67 WHERE id = 175 and 6.67 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 33 WHERE id = 175;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (20, 134, 72, 5.77);
UPDATE BOOK SET maxImportPrice = 5.77 WHERE id = 134 and 5.77 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 72 WHERE id = 134;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (20, 176, 83, 8.84);
UPDATE BOOK SET maxImportPrice = 8.84 WHERE id = 176 and 8.84 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 83 WHERE id = 176;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (20, 60, 95, 9.21);
UPDATE BOOK SET maxImportPrice = 9.21 WHERE id = 60 and 9.21 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 95 WHERE id = 60;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (20, 61, 51, 7.45);
UPDATE BOOK SET maxImportPrice = 7.45 WHERE id = 61 and 7.45 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 51 WHERE id = 61;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (20, 98, 34, 7.53);
UPDATE BOOK SET maxImportPrice = 7.53 WHERE id = 98 and 7.53 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 34 WHERE id = 98;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (20, 71, 76, 9.33);
UPDATE BOOK SET maxImportPrice = 9.33 WHERE id = 71 and 9.33 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 76 WHERE id = 71;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (20, 97, 38, 8.15);
UPDATE BOOK SET maxImportPrice = 8.15 WHERE id = 97 and 8.15 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 38 WHERE id = 97;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (20, 133, 95, 9.12);
UPDATE BOOK SET maxImportPrice = 9.12 WHERE id = 133 and 9.12 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 95 WHERE id = 133;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (20, 89, 53, 7.49);
UPDATE BOOK SET maxImportPrice = 7.49 WHERE id = 89 and 7.49 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 53 WHERE id = 89;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (20, 80, 64, 8.18);
UPDATE BOOK SET maxImportPrice = 8.18 WHERE id = 80 and 8.18 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 64 WHERE id = 80;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (20, 62, 98, 7.01);
UPDATE BOOK SET maxImportPrice = 7.01 WHERE id = 62 and 7.01 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 98 WHERE id = 62;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (20, 74, 42, 10.45);
UPDATE BOOK SET maxImportPrice = 10.45 WHERE id = 74 and 10.45 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 42 WHERE id = 74;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (20, 70, 22, 8.37);
UPDATE BOOK SET maxImportPrice = 8.37 WHERE id = 70 and 8.37 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 22 WHERE id = 70;
UPDATE ImportSheet set totalCost = 9612.9 where id = 20;
INSERT INTO ImportSheet (id, employeeInChargeId, importDate, totalCost) VALUES (21, 4, '2024-03-04', 0);
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (21, 37, 64, 6.25);
UPDATE BOOK SET maxImportPrice = 6.25 WHERE id = 37 and 6.25 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 64 WHERE id = 37;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (21, 158, 96, 10.29);
UPDATE BOOK SET maxImportPrice = 10.29 WHERE id = 158 and 10.29 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 96 WHERE id = 158;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (21, 110, 45, 8.38);
UPDATE BOOK SET maxImportPrice = 8.38 WHERE id = 110 and 8.38 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 45 WHERE id = 110;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (21, 130, 25, 6.53);
UPDATE BOOK SET maxImportPrice = 6.53 WHERE id = 130 and 6.53 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 25 WHERE id = 130;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (21, 141, 37, 8.37);
UPDATE BOOK SET maxImportPrice = 8.37 WHERE id = 141 and 8.37 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 37 WHERE id = 141;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (21, 172, 84, 9.36);
UPDATE BOOK SET maxImportPrice = 9.36 WHERE id = 172 and 9.36 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 84 WHERE id = 172;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (21, 155, 62, 9.36);
UPDATE BOOK SET maxImportPrice = 9.36 WHERE id = 155 and 9.36 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 62 WHERE id = 155;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (21, 87, 43, 7.27);
UPDATE BOOK SET maxImportPrice = 7.27 WHERE id = 87 and 7.27 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 43 WHERE id = 87;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (21, 19, 48, 9.42);
UPDATE BOOK SET maxImportPrice = 9.42 WHERE id = 19 and 9.42 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 48 WHERE id = 19;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (21, 12, 26, 9.93);
UPDATE BOOK SET maxImportPrice = 9.93 WHERE id = 12 and 9.93 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 26 WHERE id = 12;
UPDATE ImportSheet set totalCost = 4627.39 where id = 21;
INSERT INTO ImportSheet (id, employeeInChargeId, importDate, totalCost) VALUES (22, 3, '2024-04-08', 0);
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (22, 45, 30, 5.87);
UPDATE BOOK SET maxImportPrice = 5.87 WHERE id = 45 and 5.87 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 30 WHERE id = 45;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (22, 151, 22, 9.88);
UPDATE BOOK SET maxImportPrice = 9.88 WHERE id = 151 and 9.88 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 22 WHERE id = 151;
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (22, 149, 27, 10.15);
UPDATE BOOK SET maxImportPrice = 10.15 WHERE id = 149 and 10.15 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 27 WHERE id = 149;
UPDATE ImportSheet set totalCost = 667.51 where id = 22;
INSERT INTO ImportSheet (id, employeeInChargeId, importDate, totalCost) VALUES (23, 4, '2024-05-01', 0);
INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (23, 103, 100, 8.76);
UPDATE BOOK SET maxImportPrice = 8.76 WHERE id = 103 and 8.76 > COALESCE(maxImportPrice, 0);
UPDATE BOOK SET quantity = quantity + 100 WHERE id = 103;
UPDATE ImportSheet set totalCost = 876 where id = 23;

INSERT INTO OrderSheet (id, memberId, employeeInChargeId, orderDate, discountedTotalCost) VALUES (1, 101, 3, '2024-02-20', 0);
INSERT INTO OrderedBook (orderSheetId, bookId, quantity, pricePerBook) VALUES 
(1, (SELECT id FROM Book WHERE title = 'Dandadan, Vol. 1 (2022)'), 1, 9),
(1, (SELECT id FROM Book WHERE title = 'Dandadan, Vol. 10 (2024)'), 1, 9.28),
(1, (SELECT id FROM Book WHERE title = 'Dandadan, Vol. 9 (2024)'), 1, 7.87);
UPDATE OrderSheet set discountedTotalCost = 26.15 where id = 1;
INSERT INTO OrderSheet (id, memberId, employeeInChargeId, orderDate, discountedTotalCost) VALUES (2, 2, 3, '2024-01-12', 0);
INSERT INTO OrderedBook (orderSheetId, bookId, quantity, pricePerBook) VALUES (2, (SELECT id FROM Book WHERE title = 'Yotsuba&!, Vol. 10 (2011)'), 1, 10.02),
(2, (SELECT id FROM Book WHERE title = 'Yotsuba&!, Vol. 11 (2012)'), 1, 7.52),
(2, (SELECT id FROM Book WHERE title = 'Yotsuba&!, Vol. 12 (2013)'), 1, 6.54);
UPDATE OrderSheet set discountedTotalCost = 22.876 where id = 2;
INSERT INTO OrderSheet (id, memberId, employeeInChargeId, orderDate, discountedTotalCost) VALUES (3, 101, 3, '2024-01-12', 0);
INSERT INTO OrderedBook (orderSheetId, bookId, quantity, pricePerBook) VALUES (3, (SELECT id FROM Book WHERE title = 'Delicious in Dungeon, Vol. 1 (2017)'), 1, 6.12),
(3, (SELECT id FROM Book WHERE title = 'Delicious in Dungeon, Vol. 4 (2018)'), 1, 7.80),
(3, (SELECT id FROM Book WHERE title = 'Delicious in Dungeon, Vol. 5 (2018)'), 1, 11.48);
UPDATE OrderSheet set discountedTotalCost = 25.4 where id = 3;
INSERT INTO OrderSheet (id, memberId, employeeInChargeId, orderDate, discountedTotalCost) VALUES (4, 25, 3, '2024-02-20', 0);
INSERT INTO OrderedBook (orderSheetId, bookId, quantity, pricePerBook) VALUES (4, (SELECT id FROM Book WHERE title = 'Love, Theoretically (2023)'), 1, 10.17),(4, (SELECT id FROM Book WHERE title = 'Love on the Brain (2022)'), 1, 11.35),(4, (SELECT id FROM Book WHERE title = 'The Murder of Roger Ackroyd & The Hollow Bundle (2022)'), 1, 9.63);UPDATE OrderSheet set discountedTotalCost = 29.5925 where id = 4;
INSERT INTO OrderSheet (id, memberId, employeeInChargeId, orderDate, discountedTotalCost) VALUES (7, 6, 10, '2024-04-29', 0);
INSERT INTO OrderedBook (orderSheetId, bookId, quantity, pricePerBook) VALUES (7, (SELECT id FROM Book WHERE title = 'Dandadan, Vol. 9 (2024)'),1,7.87),
(7, (SELECT id FROM Book WHERE title = 'One Piece, Vol. 99 (2022)'),1,7.87),
(7, (SELECT id FROM Book WHERE title = 'The Grapes of Wrath - 75th Anniversary Edition (2014)'),6,6.1),
(7, (SELECT id FROM Book WHERE title = 'One Piece, Vol. 95 - Oden''s Adventure (2020)'),8,10.83),
(7, (SELECT id FROM Book WHERE title = 'The Nature Of Middle-Earth (2021)'),2,7.76),
(7, (SELECT id FROM Book WHERE title = 'The Collected Poems of J. R. R. Tolkien (2024)'),2,8.21),
(7, (SELECT id FROM Book WHERE title = 'The Hobbit Collector''s Edition (2024)'),5,11.42),
(7, (SELECT id FROM Book WHERE title = 'The Lord of the Rings Illustrated (2021)'),10,8.47);
UPDATE OrderSheet set discountedTotalCost = 312.72 where id = 7;
INSERT INTO OrderSheet (id, memberId, employeeInChargeId, orderDate, discountedTotalCost) VALUES (8, 6, 7, '2024-04-30', 0);
INSERT INTO OrderedBook (orderSheetId, bookId, quantity, pricePerBook) VALUES (8, (SELECT id FROM Book WHERE title = 'The Apothecary Diaries: Volume 10 (Light Novel) (2024)'),10,8.4),
(8, (SELECT id FROM Book WHERE title = 'Blue Period 8 (2022)'),8,10.3),
(8, (SELECT id FROM Book WHERE title = 'Yotsuba&!, Vol. 13 (2016)'),10,10.43),
(8, (SELECT id FROM Book WHERE title = 'Yotsuba&!, Vol. 10 (2011)'),7,10.02),
(8, (SELECT id FROM Book WHERE title = 'Frieren: Beyond Journey’s End, Vol. 2 (2022)'),3,11.1),
(8, (SELECT id FROM Book WHERE title = 'One Piece, Vol. 97 - My Bible (2021)'),6,6.16),
(8, (SELECT id FROM Book WHERE title = 'One Piece, Vol. 98 - Vassals Of Glory (2021)'),3,8.23),
(8, (SELECT id FROM Book WHERE title = 'The Apothecary Diaries: Volume 8 (Light Novel) (2023)'),7,11.03);
UPDATE OrderSheet set discountedTotalCost = 513 where id = 8;
INSERT INTO OrderSheet (id, memberId, employeeInChargeId, orderDate, discountedTotalCost) VALUES (9, 18, 12, '2024-04-07', 0);
INSERT INTO OrderedBook (orderSheetId, bookId, quantity, pricePerBook) VALUES (9, (SELECT id FROM Book WHERE title = 'The Apothecary Diaries 01 (Manga) (2020)'),4,8.79),
(9, (SELECT id FROM Book WHERE title = 'Dandadan, Vol. 5 (2023)'),2,10.76),
(9, (SELECT id FROM Book WHERE title = 'Skip and Loafer Vol. 8 (2023)'),5,9.89),
(9, (SELECT id FROM Book WHERE title = 'Mark Twain, the Globetrotter: Complete Travel Books, Memoirs & Anecdotes (Illustrated Edition) (2024)'),8,10.14),
(9, (SELECT id FROM Book WHERE title = 'Frieren: Beyond Journey’s End, Vol. 2 (2022)'),7,11.1),
(9, (SELECT id FROM Book WHERE title = 'The Institute - A Novel (2021)'),2,10.92),
(9, (SELECT id FROM Book WHERE title = 'Voyaging, Volume One: The Plague Star (2023)'),8,11.48),
(9, (SELECT id FROM Book WHERE title = 'One Piece, Vol. 99 (2022)'),10,7.87);
UPDATE OrderSheet set discountedTotalCost = 457.33 where id = 9;
INSERT INTO OrderSheet (id, memberId, employeeInChargeId, orderDate, discountedTotalCost) VALUES (10, 6, 10, '2024-04-10', 0);
INSERT INTO OrderedBook (orderSheetId, bookId, quantity, pricePerBook) VALUES (10, (SELECT id FROM Book WHERE title = 'The Works of William Shakespeare - Vol. V (2023)'),6,8.6),
(10, (SELECT id FROM Book WHERE title = 'The Peoples of Middle-earth (2023)'),9,10.38),
(10, (SELECT id FROM Book WHERE title = 'The Silmarillion (2022)'),2,8.41),
(10, (SELECT id FROM Book WHERE title = 'The Grapes of Wrath - 75th Anniversary Edition (2014)'),3,6.1),
(10, (SELECT id FROM Book WHERE title = 'Magic (2023)'),2,8.67),
(10, (SELECT id FROM Book WHERE title = 'Frieren: Beyond Journey''s End, Vol. 1 (2021)'),9,7.24),
(10, (SELECT id FROM Book WHERE title = 'The Love Hypothesis (2021)'),8,6.98),
(10, (SELECT id FROM Book WHERE title = 'Old Times on the Mississippi (2020)'),10,8.21);
UPDATE OrderSheet set discountedTotalCost = 400.58 where id = 10;
INSERT INTO OrderSheet (id, memberId, employeeInChargeId, orderDate, discountedTotalCost) VALUES (11, 54, 10, '2024-04-25', 0);
INSERT INTO OrderedBook (orderSheetId, bookId, quantity, pricePerBook) VALUES (11, (SELECT id FROM Book WHERE title = 'The RSC Shakespeare: the Complete Works (2022)'),5,9.69),
(11, (SELECT id FROM Book WHERE title = 'Skip and Loafer Vol. 7 (2023)'),5,9.8),
(11, (SELECT id FROM Book WHERE title = 'The Love Hypothesis (2021)'),3,6.98),
(11, (SELECT id FROM Book WHERE title = 'Cherry Magic! Thirty Years of Virginity Can Make You a Wizard?! 01 (2020)'),2,9.99),
(11, (SELECT id FROM Book WHERE title = 'The Complete Hard Case Crime Stephen King Collection (2021)'),7,8.05),
(11, (SELECT id FROM Book WHERE title = 'The Two Towers [Tv Tie-In]: The Lord of the Rings Part Two (2022)'),9,11.49),
(11, (SELECT id FROM Book WHERE title = 'More Than Just a House (2024)'),5,9.69),
(11, (SELECT id FROM Book WHERE title = 'The Ickabog - A warm and witty fairy-tale adventure to entertain the whole family (2020)'),6,6.32);
UPDATE OrderSheet set discountedTotalCost = 384.5 where id = 11;
INSERT INTO OrderSheet (id, memberId, employeeInChargeId, orderDate, discountedTotalCost) VALUES (12, 6, 10, '2024-03-07', 0);
INSERT INTO OrderedBook (orderSheetId, bookId, quantity, pricePerBook) VALUES (12, (SELECT id FROM Book WHERE title = 'Tatsuki Fujimoto Before Chainsaw Man: 17-21 (2023)'),10,8.86),
(12, (SELECT id FROM Book WHERE title = 'Wild Cards XI: Dealer''s Choice - Book Three of the Rox Triad (2020)'),2,9.31),
(12, (SELECT id FROM Book WHERE title = 'Skip and Loafer Vol. 8 (2023)'),10,9.89),
(12, (SELECT id FROM Book WHERE title = 'One Piece, Vol. 99 - Straw Hat Luffy (2022)'),10,8.56),
(12, (SELECT id FROM Book WHERE title = 'Tatsuki Fujimoto Before Chainsaw Man: 22-26 (2023)'),7,11.16),
(12, (SELECT id FROM Book WHERE title = 'One Piece, Vol. 96 - I Am Oden, And I Was Born To Boil (2021)'),7,6.02),
(12, (SELECT id FROM Book WHERE title = 'Yotsuba&!, Vol. 11 (2012)'),2,7.52),
(12, (SELECT id FROM Book WHERE title = 'One Piece, Vol. 95 - Oden''s Adventure (2020)'),2,10.83);
UPDATE OrderSheet set discountedTotalCost = 448.68 where id = 12;
INSERT INTO OrderSheet (id, memberId, employeeInChargeId, orderDate, discountedTotalCost) VALUES (13, 19, 4, '2024-03-30', 0);
INSERT INTO OrderedBook (orderSheetId, bookId, quantity, pricePerBook) VALUES (13, (SELECT id FROM Book WHERE title = 'To Kill a Mockingbird: A Graphic Novel - A Graphic Novel (2018)'),8,6.02),
(13, (SELECT id FROM Book WHERE title = 'One Piece Box Set 4: Dressrosa to Reverie - Volumes 71-90 with Premium (2021)'),7,7.25),
(13, (SELECT id FROM Book WHERE title = 'King John - Third Series (2018)'),4,11.09),
(13, (SELECT id FROM Book WHERE title = 'Harry Potter Slytherin House Editions Hardback Box Set (2021)'),2,10.04),
(13, (SELECT id FROM Book WHERE title = 'One Piece, Vol. 95 - Oden''s Adventure (2020)'),8,10.83),
(13, (SELECT id FROM Book WHERE title = 'Cannery Row (2020)'),9,7.99),
(13, (SELECT id FROM Book WHERE title = 'Mark Twain Essays Annotated (2021)'),7,10.52),
(13, (SELECT id FROM Book WHERE title = 'Frieren: Beyond Journey’s End, Vol. 2 (2022)'),8,11.1);
UPDATE OrderSheet set discountedTotalCost = 484.34 where id = 13;
INSERT INTO OrderSheet (id, memberId, employeeInChargeId, orderDate, discountedTotalCost) VALUES (14, 19, 6, '2024-03-18', 0);
INSERT INTO OrderedBook (orderSheetId, bookId, quantity, pricePerBook) VALUES (14, (SELECT id FROM Book WHERE title = 'Delicious in Dungeon, Vol. 9 (2021)'),2,9.97),
(14, (SELECT id FROM Book WHERE title = 'Harry Potter and the Prisoner of Azkaban (2020)'),5,6.75),
(14, (SELECT id FROM Book WHERE title = 'Of Mice and Men: Teacher''s Deluxe Edition (2013)'),8,8.65),
(14, (SELECT id FROM Book WHERE title = 'On Earth We''re Briefly Gorgeous - A Novel (2021)'),10,8.01),
(14, (SELECT id FROM Book WHERE title = 'Mickey Mouse Funhouse: Adventures in Dino-Sitting (2023)'),7,11.82),
(14, (SELECT id FROM Book WHERE title = 'Yotsuba&!, Vol. 13 (2016)'),9,10.43),
(14, (SELECT id FROM Book WHERE title = 'Dandadan, Vol. 5 (2023)'),6,10.76),
(14, (SELECT id FROM Book WHERE title = 'Dandadan, Vol. 7 (2024)'),3,7.4);
UPDATE OrderSheet set discountedTotalCost = 466.36 where id = 14;
INSERT INTO OrderSheet (id, memberId, employeeInChargeId, orderDate, discountedTotalCost) VALUES (15, 78, 7, '2024-04-17', 0);
INSERT INTO OrderedBook (orderSheetId, bookId, quantity, pricePerBook) VALUES (15, (SELECT id FROM Book WHERE title = 'The Apothecary Diaries: Volume 9 (Light Novel) (2023)'),8,6.09),
(15, (SELECT id FROM Book WHERE title = 'The Adventures of Tom Bombadil (2024)'),3,8.37),
(15, (SELECT id FROM Book WHERE title = 'Dandadan, Vol. 9 (2024)'),5,7.87),
(15, (SELECT id FROM Book WHERE title = 'The Apothecary Diaries 02 (Manga) (2021)'),5,6.68),
(15, (SELECT id FROM Book WHERE title = 'One Piece, Vol. 96 - I Am Oden, And I Was Born To Boil (2021)'),7,6.02),
(15, (SELECT id FROM Book WHERE title = 'Love on the Brain (2022)'),5,11.35),
(15, (SELECT id FROM Book WHERE title = 'Fire Punch, Vol. 6 (2019)'),8,7.99),
(15, (SELECT id FROM Book WHERE title = 'Let’s Go For a Walk Outside (Super Simple Storybooks) (2023)'),9,7.35);
UPDATE OrderSheet set discountedTotalCost = 375.54 where id = 15;
INSERT INTO OrderSheet (id, memberId, employeeInChargeId, orderDate, discountedTotalCost) VALUES (16, 16, 5, '2024-03-29', 0);
INSERT INTO OrderedBook (orderSheetId, bookId, quantity, pricePerBook) VALUES (16, (SELECT id FROM Book WHERE title = 'Kissing my Co-worker (2021)'),9,6.31),
(16, (SELECT id FROM Book WHERE title = 'The Lord of the Rings Illustrated (2021)'),3,8.47),
(16, (SELECT id FROM Book WHERE title = 'Magnetism (2024)'),10,6.25),
(16, (SELECT id FROM Book WHERE title = 'Delicious in Dungeon, Vol. 7 (2019)'),5,11.33),
(16, (SELECT id FROM Book WHERE title = 'One Piece Pirate Recipes (2021)'),4,10.35),
(16, (SELECT id FROM Book WHERE title = 'The Peoples of Middle-earth (2023)'),8,10.38),
(16, (SELECT id FROM Book WHERE title = 'The Silmarillion Collector''s Edition (2024)'),8,6.9),
(16, (SELECT id FROM Book WHERE title = 'On Earth We''re Briefly Gorgeous - A Novel (2019)'),4,10.26);
UPDATE OrderSheet set discountedTotalCost = 422.03 where id = 16;
INSERT INTO OrderSheet (id, memberId, employeeInChargeId, orderDate, discountedTotalCost) VALUES (17, 71, 1, '2024-04-09', 0);
INSERT INTO OrderedBook (orderSheetId, bookId, quantity, pricePerBook) VALUES (17, (SELECT id FROM Book WHERE title = 'The Way Station (2019)'),6,7.74),
(17, (SELECT id FROM Book WHERE title = 'The Long Valley (2022)'),6,11.2),
(17, (SELECT id FROM Book WHERE title = 'Voyaging, Volume One: The Plague Star (2023)'),5,11.48),
(17, (SELECT id FROM Book WHERE title = 'The Hobbit: Illustrated by the Author (2023)'),5,8.79),
(17, (SELECT id FROM Book WHERE title = 'Mickey Mouse Funhouse: Worlds of Fun! - My First Comic Reader! (2024)'),1,7.65),
(17, (SELECT id FROM Book WHERE title = 'The Ickabog - A warm and witty fairy-tale adventure to entertain the whole family (2020)'),2,6.32),
(17, (SELECT id FROM Book WHERE title = 'Is This a Dagger Which I See Before Me? (2016)'),7,8.04),
(17, (SELECT id FROM Book WHERE title = 'Delicious in Dungeon, Vol. 8 (2020)'),2,7.58);
UPDATE OrderSheet set discountedTotalCost = 306.72 where id = 17;
INSERT INTO OrderSheet (id, memberId, employeeInChargeId, orderDate, discountedTotalCost) VALUES (18, 4, 12, '2024-04-11', 0);
INSERT INTO OrderedBook (orderSheetId, bookId, quantity, pricePerBook) VALUES (18, (SELECT id FROM Book WHERE title = 'William Shakespeare Tragedies (2020)'),7,9.52),
(18, (SELECT id FROM Book WHERE title = 'To Kill a Mockingbird: A Graphic Novel (2018)'),9,6.87),
(18, (SELECT id FROM Book WHERE title = 'Mansfield Park - Jane Austen Classic (annotated) (2021)'),5,6.38),
(18, (SELECT id FROM Book WHERE title = 'The Hobbit Deluxe Illustrated Edition (2024)'),5,7.75),
(18, (SELECT id FROM Book WHERE title = 'The Game Changer (2020)'),8,9.95),
(18, (SELECT id FROM Book WHERE title = 'Delicious in Dungeon, Vol. 8 (2020)'),9,7.58),
(18, (SELECT id FROM Book WHERE title = 'My First Disney Classics: A Christmas Carol (2023)'),6,7.31),
(18, (SELECT id FROM Book WHERE title = 'Frieren: Beyond Journey’s End, Vol. 2 (2022)'),10,11.1);
UPDATE OrderSheet set discountedTotalCost = 501.8 where id = 18;
INSERT INTO OrderSheet (id, memberId, employeeInChargeId, orderDate, discountedTotalCost) VALUES (19, 70, 7, '2024-03-08', 0);
INSERT INTO OrderedBook (orderSheetId, bookId, quantity, pricePerBook) VALUES (19, (SELECT id FROM Book WHERE title = 'One Piece, Vol. 99 (2022)'),6,7.87),
(19, (SELECT id FROM Book WHERE title = 'The Mysterious Affair at Styles Agatha Christie - Original Ending (2021)'),6,7.56),
(19, (SELECT id FROM Book WHERE title = 'The Apothecary Diaries 01 (Manga) (2020)'),8,8.79),
(19, (SELECT id FROM Book WHERE title = 'Time Is a Mother (2022)'),10,8.2),
(19, (SELECT id FROM Book WHERE title = 'The Silmarillion (2022)'),5,8.41),
(19, (SELECT id FROM Book WHERE title = 'The Way Station (2019)'),6,7.74),
(19, (SELECT id FROM Book WHERE title = 'On Earth We''re Briefly Gorgeous - A Novel (2019)'),3,10.26);
UPDATE OrderSheet set discountedTotalCost = 379.91 where id = 19;
INSERT INTO OrderSheet (id, memberId, employeeInChargeId, orderDate, discountedTotalCost) VALUES (20, 17, 4, '2024-03-30', 0);
INSERT INTO OrderedBook (orderSheetId, bookId, quantity, pricePerBook) VALUES (20, (SELECT id FROM Book WHERE title = 'Cherry Magic! Thirty Years of Virginity Can Make You a Wizard?! 04 (2022)'),1,10.12),
(20, (SELECT id FROM Book WHERE title = 'My First Disney Classics: A Christmas Carol (2023)'),2,7.31),
(20, (SELECT id FROM Book WHERE title = 'I, Robot (2018)'),10,7.7),
(20, (SELECT id FROM Book WHERE title = 'The Two Towers [Tv Tie-In]: The Lord of the Rings Part Two (2022)'),10,11.49),
(20, (SELECT id FROM Book WHERE title = 'Frieren: Beyond Journey''s End, Vol. 7 (2023)'),5,7.44),
(20, (SELECT id FROM Book WHERE title = 'The RSC Shakespeare: the Complete Works (2022)'),3,9.69),
(20, (SELECT id FROM Book WHERE title = 'Mickey Mouse Funhouse: Worlds of Fun! - My First Comic Reader! (2024)'),6,7.65),
(20, (SELECT id FROM Book WHERE title = 'The Silmarillion Collector''s Edition (2024)'),9,6.9);
UPDATE OrderSheet set discountedTotalCost = 390.91 where id = 20;
INSERT INTO OrderSheet (id, memberId, employeeInChargeId, orderDate, discountedTotalCost) VALUES (21, 53, 11, '2024-03-30', 0);
INSERT INTO OrderedBook (orderSheetId, bookId, quantity, pricePerBook) VALUES (21, (SELECT id FROM Book WHERE title = 'The Grapes of Wrath - 75th Anniversary Edition (2014)'),7,6.1),
(21, (SELECT id FROM Book WHERE title = 'Magnetism (2024)'),8,6.25),
(21, (SELECT id FROM Book WHERE title = 'The Collected Poems of J. R. R. Tolkien (2024)'),3,8.21);
UPDATE OrderSheet set discountedTotalCost = 117.33 where id = 21;
INSERT INTO OrderSheet (id, memberId, employeeInChargeId, orderDate, discountedTotalCost) VALUES (22, 59, 1, '2024-03-18', 0);
INSERT INTO OrderedBook (orderSheetId, bookId, quantity, pricePerBook) VALUES (22, (SELECT id FROM Book WHERE title = 'Time Is a Mother (2022)'),2,8.2);
UPDATE OrderSheet set discountedTotalCost = 16.4 where id = 22;
INSERT INTO OrderSheet (id, memberId, employeeInChargeId, orderDate, discountedTotalCost) VALUES (23, 52, 11, '2024-03-04', 0);
INSERT INTO OrderedBook (orderSheetId, bookId, quantity, pricePerBook) VALUES (23, (SELECT id FROM Book WHERE title = 'Is This a Dagger Which I See Before Me? (2016)'),2,8.04),
(23, (SELECT id FROM Book WHERE title = 'Delicious in Dungeon, Vol. 8 (2020)'),6,7.58),
(23, (SELECT id FROM Book WHERE title = 'Dandadan, Vol. 4 (2023)'),5,11.71),
(23, (SELECT id FROM Book WHERE title = 'The Grapes of Wrath 75th Anniversary Edition (Limited edition) (2014)'),1,11.3);
UPDATE OrderSheet set discountedTotalCost = 131.41 where id = 23;
INSERT INTO OrderSheet (id, memberId, employeeInChargeId, orderDate, discountedTotalCost) VALUES (24, 67, 2, '2024-04-14', 0);
INSERT INTO OrderedBook (orderSheetId, bookId, quantity, pricePerBook) VALUES (24, (SELECT id FROM Book WHERE title = 'The Grapes of Wrath 75th Anniversary Edition (Limited edition) (2014)'),6,11.3);
UPDATE OrderSheet set discountedTotalCost = 67.8 where id = 24;
INSERT INTO OrderSheet (id, memberId, employeeInChargeId, orderDate, discountedTotalCost) VALUES (25, 4, 8, '2024-03-16', 0);
INSERT INTO OrderedBook (orderSheetId, bookId, quantity, pricePerBook) VALUES (25, (SELECT id FROM Book WHERE title = 'Delicious in Dungeon, Vol. 8 (2020)'),6,7.58);
UPDATE OrderSheet set discountedTotalCost = 45.48 where id = 25;
INSERT INTO OrderSheet (id, memberId, employeeInChargeId, orderDate, discountedTotalCost) VALUES (26, 48, 11, '2024-04-10', 0);
INSERT INTO OrderedBook (orderSheetId, bookId, quantity, pricePerBook) VALUES (26, (SELECT id FROM Book WHERE title = 'Yotsuba&!, Vol. 13 (2016)'),4,10.43),
(26, (SELECT id FROM Book WHERE title = 'The Lord of the Rings (2022)'),4,11.36),
(26, (SELECT id FROM Book WHERE title = 'The Letters of J. R. R. Tolkien (2023)'),9,8.41);
UPDATE OrderSheet set discountedTotalCost = 162.85 where id = 26;
INSERT INTO OrderSheet (id, memberId, employeeInChargeId, orderDate, discountedTotalCost) VALUES (27, 35, 8, '2024-04-22', 0);
INSERT INTO OrderedBook (orderSheetId, bookId, quantity, pricePerBook) VALUES (27, (SELECT id FROM Book WHERE title = 'Dandadan, Vol. 5 (2023)'),10,10.76),
(27, (SELECT id FROM Book WHERE title = 'Wild Cards XI: Dealer''s Choice - Book Three of the Rox Triad (2020)'),1,9.31),
(27, (SELECT id FROM Book WHERE title = 'Midsummer Mysteries - Tales from the Queen of Mystery (2023)'),8,10.5),
(27, (SELECT id FROM Book WHERE title = 'Kissing my Co-worker (2021)'),10,6.31),
(27, (SELECT id FROM Book WHERE title = 'The Apothecary Diaries: Volume 8 (Light Novel) (2023)'),2,11.03),
(27, (SELECT id FROM Book WHERE title = 'Cannery Row (2020)'),2,7.99),
(27, (SELECT id FROM Book WHERE title = 'Frieren: Beyond Journey’s End, Vol. 6 (2022)'),1,8.73),
(27, (SELECT id FROM Book WHERE title = 'Frieren: Beyond Journey''s End, Vol. 5 (2022)'),9,9.13);
UPDATE OrderSheet set discountedTotalCost = 392.95 where id = 27;
INSERT INTO OrderSheet (id, memberId, employeeInChargeId, orderDate, discountedTotalCost) VALUES (28, 87, 11, '2024-04-14', 0);
INSERT INTO OrderedBook (orderSheetId, bookId, quantity, pricePerBook) VALUES (28, (SELECT id FROM Book WHERE title = 'My Daddy (Peppa Pig) (2018)'),7,6.64),
(28, (SELECT id FROM Book WHERE title = 'Blue Period 8 (2022)'),3,10.3),
(28, (SELECT id FROM Book WHERE title = 'More Than Just a House (2024)'),10,9.69),
(28, (SELECT id FROM Book WHERE title = 'Mickey Mouse Funhouse: Adventures in Dino-Sitting (2023)'),5,11.82);
UPDATE OrderSheet set discountedTotalCost = 233.38 where id = 28;

DROP TRIGGER IF EXISTS update_saleprice_trigger ON OrderedBook;
DROP TRIGGER IF EXISTS update_quantity_postorder_trigger ON OrderedBook;
DROP TRIGGER IF EXISTS update_saleprice_postimport_trigger ON OrderedBook;

SELECT setval('account_id_seq', (SELECT MAX(id) FROM Account) + 1);
SELECT setval('publisher_id_seq', (SELECT MAX(id) FROM Publisher) + 1);
SELECT setval('author_id_seq', (SELECT MAX(id) FROM Author) + 1);
SELECT setval('book_id_seq', (SELECT MAX(id) FROM Book) + 1);
SELECT setval('category_id_seq', (SELECT MAX(id) FROM Category) + 1);
SELECT setval('importsheet_id_seq', (SELECT MAX(id) FROM ImportSheet) + 1);
SELECT setval('member_id_seq', (SELECT MAX(id) FROM Member) + 1);
SELECT setval('ordersheet_id_seq', (SELECT MAX(id) FROM OrderSheet) + 1);
