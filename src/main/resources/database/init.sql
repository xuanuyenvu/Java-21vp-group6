-- Instruction
-- 0. If there is existing database, please delete and create a new one
-- Please also check if psql exists in your PATH by 'psql --help'. If not, please install and put it in your path
-- 1. Create a new table bsms with owner bsms
-- 2. Create new folder in "C:/Users/Public/" named "Public Data"
-- 3. Copy all the csv files in src/main/resources/database to "Public Data"
-- 4. Open terminal, enter "psql -U bsms bsms", enter password
-- 5. Enter "\encoding utf8"
-- 6. \i src/main/resources/database/init.sql
-- Please screenshot if there are errors

-- The results of the select count(*) for each table:
-- 189 book
-- 31 author
-- 24 publisher
-- 23 category
-- 316 book category
-- 4 importsheet
-- 39 importedbook
-- 101 member
-- 12 account, 3 of which admin
-- 4 ordersheet
-- 12 orderedbook

ALTER DATABASE bsms SET ENCODING TO 'UTF8';

-- Tables
create table if not exists Account (
    id serial primary key,
    phone char(10) unique check (phone ~ '^[0-9]{10}$') not null,
    password varchar(255) check (length(password) > 4) not null,
    name varchar(255) check (length(name) > 0) not null,
    gender varchar(10) check (gender in ('Male', 'Female', 'Other')) not null,
    email varchar(255) unique check (email ~ '^[A-Za-z0-9._%-]+@[A-Za-z0-9.-]+$'),
    address varchar(255) check (length(address) > 0),
    isAdmin boolean default false not null,
    isLocked boolean default false not null
);

create table if not exists Publisher (
    id serial primary key,
    name varchar(255) unique check (length(name) > 0) not null,
    email varchar(255) unique check (email ~ '^[A-Za-z0-9._%-]+@[A-Za-z0-9.-]+$'),
    address varchar(255) check (length(address) > 0),
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
    salePrice decimal(12, 2) check (salePrice >= 0) not null,
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

-- Publisher
DO $$BEGIN
 BEGIN
    COPY Publisher(name, email, address, ishidden)
    FROM 'C:/Users/Public/Public Data/publisher.csv'
    DELIMITER ';'
    csv header encoding 'UTF8';
 EXCEPTION
    WHEN others THEN
      RAISE EXCEPTION 'Failed to copy data: %', SQLERRM;
 END;
END;$$;

-- Author
DO $$BEGIN
 BEGIN
    COPY Author(name, overview, ishidden)
    FROM 'C:/Users/Public/Public Data/author.csv'
    DELIMITER ';'
    csv header encoding 'UTF8';
 EXCEPTION
    WHEN others THEN
      RAISE EXCEPTION 'Failed to copy data: %', SQLERRM;
 END;
END;$$;

-- Book
DO $$BEGIN
 BEGIN
    CREATE TEMP TABLE temp_books (
        id serial primary key,
        title varchar(255) unique check (length(title) > 0) not null,
        authorName varchar(255) not null,
        publisherName varchar(255) not null,
        pageCount int check (pageCount >= 0) not null,
        publishDate date check (publishDate < current_date) not null,
        dimension varchar(30) check (dimension ~ '^[0-9]+(\.[0-9]+)?x[0-9]+(\.[0-9]+)?x[0-9]+(\.[0-9]+)? cm$'),
        translatorName varchar(255),
        overview text,
        quantity int check (quantity >= 0) not null,
        salePrice decimal(12, 2) check (salePrice >= 0) not null,
        isHidden boolean default false not null
    );

    COPY temp_books (
        title, authorName, publisherName, pageCount, dimension, translatorName,
        overview, quantity, salePrice, publishdate, isHidden
    )
    FROM 'C:/Users/Public/Public Data/book.csv'
    DELIMITER ';'
    csv header encoding 'UTF8';

    -- Insert data from temporary table into Book table while fetching author ID from Author table
    INSERT INTO Book (
        title, authorid, publisherid, pageCount, dimension, translatorName,
        overview, quantity, salePrice, publishdate, isHidden, hiddenParentCount
    )
    SELECT 
        tb.title, 
        a.id AS authorId,
        p.id AS publisherId,
        tb.pageCount, 
        tb.dimension, 
        tb.translatorName, 
        tb.overview, 
        tb.quantity, 
        tb.salePrice, 
        tb.publishdate, 
        tb.isHidden,
        0
    FROM 
        temp_books tb
    JOIN 
        Author a ON tb.authorName = a.name
    JOIN
        Publisher p on tb.publisherName = p.name;
 EXCEPTION
    WHEN others THEN
      RAISE EXCEPTION 'Failed to copy data: %', SQLERRM;
 END;
END;$$;

-- Category
DO $$BEGIN
 BEGIN
    COPY Category(name, ishidden)
    FROM 'C:/Users/Public/Public Data/category.csv'
    DELIMITER ';'
    csv header encoding 'UTF8';
 EXCEPTION
    WHEN others THEN
      RAISE EXCEPTION 'Failed to copy data: %', SQLERRM;
 END;
END;$$;

-- BookCategory
DO $$BEGIN
 BEGIN
    CREATE TEMP TABLE temp_book_categories (
        bookTitle varchar(255) check (length(bookTitle) > 0) not null,
        categoryName varchar(255) check (length(categoryName) > 0) not null
    );

    COPY temp_book_categories (
        bookTitle, categoryName
    )
    FROM 'C:/Users/Public/Public Data/bookcategory.csv'
    DELIMITER ';'
    csv header encoding 'UTF8';

    INSERT INTO BookCategory (bookId, categoryId) 
    SELECT 
        b.id as bookId,
        c.id as categoryId
    FROM 
        temp_book_categories tbc
    JOIN 
        Book b ON tbc.bookTitle = b.title
    JOIN
        Category c ON tbc.categoryName = c.name;
 EXCEPTION
    WHEN others THEN
      RAISE EXCEPTION 'Failed to copy data: %', SQLERRM;
 END;
END;$$;

-- Account
DO $$BEGIN
 BEGIN
    COPY Account (phone, password, name, gender, email, address, isAdmin, isLocked)
    FROM 'C:/Users/Public/Public Data/account.csv'
    DELIMITER ';'
    csv header encoding 'UTF8';
 EXCEPTION
    WHEN others THEN
      RAISE EXCEPTION 'Failed to copy data: %', SQLERRM;
 END;
END;$$;

-- Member
DO $$BEGIN
 BEGIN
    COPY Member (phone, name, gender, dateOfBirth, email, address)
    FROM 'C:/Users/Public/Public Data/member.csv'
    DELIMITER ';'
    csv header encoding 'UTF8';
 EXCEPTION
    WHEN others THEN
      RAISE EXCEPTION 'Failed to copy data: %', SQLERRM;
 END;
END;$$;

-- Import books from importedBook1 
DO $$BEGIN
 DECLARE
    importSheetId INTEGER;
    total DECIMAL(12, 2);
 BEGIN
    -- Step 1: Insert into ImportSheet and get the ID
    INSERT INTO ImportSheet (employeeInChargeId, importDate, totalCost)
    VALUES (
        (SELECT id FROM Account WHERE phone = '6227898268'), 
        '2024-01-01', 
        0
    )
    RETURNING id INTO importSheetId;

    -- Step 2: Create a temporary table and load the CSV data
    CREATE TEMP TABLE temp_imported_books (
        bookTitle char(255) not null,
        quantity int check (quantity >= 0) not null,
        pricePerBook decimal(12, 2) check (pricePerBook >= 0) not null
    );

    COPY temp_imported_books (bookTitle, quantity, pricePerBook) 
    FROM 'C:/Users/Public/Public Data/importedbook1.csv'
    DELIMITER ';';

    SELECT SUM(pricePerBook * quantity) INTO total
    FROM temp_imported_books;

    UPDATE book b
    SET quantity = b.quantity + sub.total_quantity
    FROM (
        SELECT bookTitle, SUM(quantity) as total_quantity
        FROM temp_imported_books
        GROUP BY bookTitle
    ) sub
    WHERE b.title = sub.bookTitle;

    -- Step 3: Insert into ImportedBook using the temporary table and the ID from ImportSheet
    INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook)
    SELECT 
        importSheetId, 
        b.id as bookId, 
        tib.quantity, 
        pricePerBook
    FROM 
        temp_imported_books tib
    JOIN
        book b on b.title = tib.bookTitle;

    UPDATE ImportSheet
    SET totalCost = total
    WHERE id = importSheetId;
 EXCEPTION
    WHEN others THEN
      RAISE EXCEPTION 'Failed to copy data: %', SQLERRM;
 END;
END;$$;

-- Import books from importedBook2
DO $$BEGIN
 DECLARE
    importSheetId INTEGER;
    total DECIMAL(12, 2);
 BEGIN
    -- Step 1: Insert into ImportSheet and get the ID
    INSERT INTO ImportSheet (employeeInChargeId, importDate, totalCost)
    VALUES (
        (SELECT id FROM Account WHERE phone = '9138407685'), 
        '2024-01-10', 
        0
    )
    RETURNING id INTO importSheetId;

    -- Step 2: Create a temporary table and load the CSV data
    DROP TABLE IF EXISTS temp_imported_books;
    CREATE TEMP TABLE temp_imported_books (
        bookTitle char(255) not null,
        quantity int check (quantity >= 0) not null,
        pricePerBook decimal(12, 2) check (pricePerBook >= 0) not null
    );

    COPY temp_imported_books (bookTitle, quantity, pricePerBook) 
    FROM 'C:/Users/Public/Public Data/importedbook2.csv'
    DELIMITER ';';

    SELECT SUM(pricePerBook * quantity) INTO total
    FROM temp_imported_books;

    UPDATE book b
    SET quantity = b.quantity + sub.total_quantity
    FROM (
        SELECT bookTitle, SUM(quantity) as total_quantity
        FROM temp_imported_books
        GROUP BY bookTitle
    ) sub
    WHERE b.title = sub.bookTitle;

    -- Step 3: Insert into ImportedBook using the temporary table and the ID from ImportSheet
    INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook)
    SELECT 
        importSheetId, 
        b.id as bookId, 
        tib.quantity, 
        pricePerBook
    FROM 
        temp_imported_books tib
    JOIN
        book b on b.title = tib.bookTitle;

    UPDATE ImportSheet
    SET totalCost = total
    WHERE id = importSheetId;
 EXCEPTION
    WHEN others THEN
      RAISE EXCEPTION 'Failed to copy data: %', SQLERRM;
 END;
END;$$;

-- Import books from importBook3
DO $$BEGIN
  DECLARE
    importSheetId INTEGER;
    total DECIMAL(12, 2);
  BEGIN
    -- Step 1: Insert into ImportSheet and get the ID
    INSERT INTO ImportSheet (employeeInChargeId, importDate, totalCost)
    VALUES (
        (SELECT id FROM Account WHERE phone = '8321027390'), 
        '2024-02-01', 
        0
    )
    RETURNING id INTO importSheetId;

    -- Step 2: Create a temporary table and load the CSV data
    DROP TABLE IF EXISTS temp_imported_books;
    CREATE TEMP TABLE temp_imported_books  (
        bookTitle char(255) not null,
        quantity int check (quantity >= 0) not null,
        pricePerBook decimal(12, 2) check (pricePerBook >= 0) not null
    );

    COPY temp_imported_books (bookTitle, quantity, pricePerBook) 
    FROM 'C:/Users/Public/Public Data/importedbook3.csv'
    DELIMITER ';';

    SELECT SUM(pricePerBook * quantity) INTO total
    FROM temp_imported_books;

    UPDATE book b
    SET quantity = b.quantity + sub.total_quantity
    FROM (
        SELECT bookTitle, SUM(quantity) as total_quantity
        FROM temp_imported_books
        GROUP BY bookTitle
    ) sub
    WHERE b.title = sub.bookTitle;

    -- Step 3: Insert into ImportedBook using the temporary table and the ID from ImportSheet
    INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook)
    SELECT 
        importSheetId, 
        b.id as bookId, 
        tib.quantity, 
        pricePerBook
    FROM 
        temp_imported_books tib
    JOIN
        book b on b.title = tib.bookTitle;

    UPDATE ImportSheet
    SET totalCost = total
    WHERE id = importSheetId;
  EXCEPTION
    WHEN others THEN
      RAISE EXCEPTION 'Failed to copy data: %', SQLERRM;
  END;
END;$$;

-- Import books from importBook4
DO $$BEGIN
  DECLARE
    importSheetId INTEGER;
    total DECIMAL(12, 2);
  BEGIN
    -- Step 1: Insert into ImportSheet and get the ID
    INSERT INTO ImportSheet (employeeInChargeId, importDate, totalCost)
    VALUES (
        (SELECT id FROM Account WHERE phone = '8321027390'), 
        '2024-03-01', 
        0
    )
    RETURNING id INTO importSheetId;

    -- Step 2: Create a temporary table and load the CSV data
    DROP TABLE IF EXISTS temp_imported_books;
    CREATE TEMP TABLE temp_imported_books  (
        bookTitle char(255) not null,
        quantity int check (quantity >= 0) not null,
        pricePerBook decimal(12, 2) check (pricePerBook >= 0) not null
    );

    COPY temp_imported_books (bookTitle, quantity, pricePerBook) 
    FROM 'C:/Users/Public/Public Data/importedbook4.csv'
    DELIMITER ';';

    SELECT SUM(pricePerBook * quantity) INTO total
    FROM temp_imported_books;

    UPDATE book b
    SET quantity = b.quantity + sub.total_quantity
    FROM (
        SELECT bookTitle, SUM(quantity) as total_quantity
        FROM temp_imported_books
        GROUP BY bookTitle
    ) sub
    WHERE b.title = sub.bookTitle;

    -- Step 3: Insert into ImportedBook using the temporary table and the ID from ImportSheet
    INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook)
    SELECT 
        importSheetId, 
        b.id as bookId, 
        tib.quantity, 
        pricePerBook
    FROM 
        temp_imported_books tib
    JOIN
        book b on b.title = tib.bookTitle;

    UPDATE ImportSheet
    SET totalCost = total
    WHERE id = importSheetId;
  EXCEPTION
    WHEN others THEN
      RAISE EXCEPTION 'Failed to copy data: %', SQLERRM;
  END;
END;$$;


-- Order 1
DO $$BEGIN
  DECLARE
    orderSheetId INTEGER;
    discountedTotalCost DECIMAL(12, 2);
  BEGIN

    INSERT INTO OrderSheet (memberId, employeeInChargeId, orderDate, discountedTotalCost)
    VALUES (
        (SELECT id FROM Member WHERE phone = '0000000000'),
        (SELECT id FROM Account WHERE phone = '2223868021'),  
        '2024-02-20', 
        0
    )
    RETURNING id INTO orderSheetId;

    insert into OrderedBook (orderSheetId, bookId, quantity, pricePerBook)  values
        (orderSheetId, (SELECT id FROM Book WHERE title = 'Dandadan, Vol. 1 (2022)'), 1, 9),
        (orderSheetId, (SELECT id FROM Book WHERE title = 'Dandadan, Vol. 10 (2024)'), 1, 9.28),
        (orderSheetId, (SELECT id FROM Book WHERE title = 'Dandadan, Vol. 9 (2024)'), 1, 7.87); 

    UPDATE OrderSheet set discountedTotalCost = (9+9.28+7.87);

  EXCEPTION
    WHEN others THEN
      RAISE EXCEPTION 'Failed to copy data: %', SQLERRM;
  END;
END;$$;

-- Order 2
DO $$BEGIN
  DECLARE
    orderSheetId INTEGER;
    discountedTotalCost DECIMAL(12, 2);
  BEGIN

    INSERT INTO OrderSheet (memberId, employeeInChargeId, orderDate, discountedTotalCost)
    VALUES (
        (SELECT id FROM Member WHERE phone = '2996009524'),
        (SELECT id FROM Account WHERE phone = '2223868021'),  
        '2024-01-12',
        0
    )
    RETURNING id INTO orderSheetId;

    insert into OrderedBook (orderSheetId, bookId, quantity, pricePerBook) values
        (orderSheetId, (SELECT id FROM Book WHERE title = 'Yotsuba&!, Vol. 10 (2011)'), 1, 10.02),
        (orderSheetId, (SELECT id FROM Book WHERE title = 'Yotsuba&!, Vol. 11 (2012)'), 1, 7.52),
        (orderSheetId, (SELECT id FROM Book WHERE title = 'Yotsuba&!, Vol. 12 (2013)'), 1, 6.54); 

    UPDATE OrderSheet set discountedTotalCost = (10.02+7.52+6.54) * 0.95;

  EXCEPTION
    WHEN others THEN
      RAISE EXCEPTION 'Failed to copy data: %', SQLERRM;
  END;
END;$$;

-- Order 3
DO $$BEGIN
  DECLARE
    orderSheetId INTEGER;
    discountedTotalCost DECIMAL(12, 2);
  BEGIN

    INSERT INTO OrderSheet (memberId, employeeInChargeId, orderDate, discountedTotalCost)
    VALUES (
        (SELECT id FROM Member WHERE phone = '0000000000'),
        (SELECT id FROM Account WHERE phone = '2223868021'),  
        '2024-01-12', 
        0
    )
    RETURNING id INTO orderSheetId;

    insert into OrderedBook (orderSheetId, bookId, quantity, pricePerBook) values
        (orderSheetId, (SELECT id FROM Book WHERE title = 'Delicious in Dungeon, Vol. 1 (2017)'), 1, 6.12),
        (orderSheetId, (SELECT id FROM Book WHERE title = 'Delicious in Dungeon, Vol. 4 (2018)'), 1, 7.80),
        (orderSheetId, (SELECT id FROM Book WHERE title = 'Delicious in Dungeon, Vol. 5 (2018)'), 1, 11.48); 

    UPDATE OrderSheet set discountedTotalCost = (6.12+7.80+11.48);

  EXCEPTION
    WHEN others THEN
      RAISE EXCEPTION 'Failed to copy data: %', SQLERRM;
  END;
END;$$;

-- Order 4
DO $$BEGIN
  DECLARE
    orderSheetId INTEGER;
    discountedTotalCost DECIMAL(12, 2);
  BEGIN

    INSERT INTO OrderSheet (memberId, employeeInChargeId, orderDate, discountedTotalCost)
    VALUES (
        (SELECT id FROM Member WHERE phone = '4983848863'),
        (SELECT id FROM Account WHERE phone = '2223868021'),  
        '2024-02-20',
        0
    )
    RETURNING id INTO orderSheetId;

    insert into OrderedBook (orderSheetId, bookId, quantity, pricePerBook) values
        (orderSheetId, (SELECT id FROM Book WHERE title = 'Love, Theoretically (2023)'), 1, 10.17),
        (orderSheetId, (SELECT id FROM Book WHERE title = 'Love on the Brain (2022)'), 1, 11.35),
        (orderSheetId, (SELECT id FROM Book WHERE title = 'The Murder of Roger Ackroyd & The Hollow Bundle (2022)'), 1, 9.63); 

    UPDATE OrderSheet set discountedTotalCost = (10.17+11.35+9.63) * 0.95;

  EXCEPTION
    WHEN others THEN
      RAISE EXCEPTION 'Failed to copy data: %', SQLERRM;
  END;
END;$$;