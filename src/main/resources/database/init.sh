directory=$(grep 'repoDirectory' 'src/main/resources/env/bsms.properties' | cut -d'=' -f2)

# Initialize tables

sql_file="${directory}/src/main/resources/database/init.sql"
result=$(psql -U bsms -d bsms -f "${sql_file}" 2>&1)

if [ $? -eq 0 ]; then
 echo "Successfully create tables (init.sql)."
else
 echo "Create tables (init.sql) failed: $result"
fi

# Publisher
sql_command="DO \$\$BEGIN
  BEGIN
    COPY Publisher(name, email, address, ishidden)
    FROM '${directory}/src/main/resources/database/publisher.csv'
    DELIMITER ';'
    CSV HEADER;
  EXCEPTION
    WHEN others THEN
      RAISE EXCEPTION 'Failed to copy data: %', SQLERRM;
  END;
END;\$\$;"

result=$(psql -U bsms -d bsms -c "${sql_command}" 2>&1)

if [ $? -eq 0 ]; then
  echo "Successfully imported publisher data."
else
  echo "Import of publisher data failed: $result"
fi

# Author

sql_command="DO \$\$BEGIN
  BEGIN
    copy Author(name, overview, ishidden)
    FROM '${directory}/src/main/resources/database/author.csv'
    delimiter ';'
    csv header;
  EXCEPTION
    WHEN others THEN
      RAISE EXCEPTION 'Failed to copy data: %', SQLERRM;
  END;
END;\$\$;"

result=$(psql -U bsms -d bsms -c "${sql_command}" 2>&1)

if [ $? -eq 0 ]; then
  echo "Successfully imported author data."
else
  echo "Import of author data failed: $result"
fi


# Book
sql_command="DO \$\$BEGIN
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
    FROM '${directory}/src/main/resources/database/book.csv'
    DELIMITER ';'
    CSV HEADER;

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
END;\$\$;"

result=$(psql -U bsms -d bsms -c "${sql_command}" 2>&1)

if [ $? -eq 0 ]; then
  echo "Successfully imported book data."
else
  echo "Import of book data failed: $result"
fi

# Category
sql_command="DO \$\$BEGIN
  BEGIN
    COPY Category(name, ishidden)
    FROM '${directory}/src/main/resources/database/category.csv'
    DELIMITER ';'
    CSV HEADER;
  EXCEPTION
    WHEN others THEN
      RAISE EXCEPTION 'Failed to copy data: %', SQLERRM;
  END;
END;\$\$;"


result=$(psql -U bsms -d bsms -c "${sql_command}" 2>&1)


if [ $? -eq 0 ]; then
  echo "Successfully imported category data."
else
  echo "Import of category data failed: $result"
fi


# BookCategory
sql_command="DO \$\$BEGIN
  BEGIN
    CREATE TEMP TABLE temp_book_categories (
        bookTitle varchar(255) check (length(bookTitle) > 0) not null,
        categoryName varchar(255) check (length(categoryName) > 0) not null
    );

    COPY temp_book_categories (
        bookTitle, categoryName
    )
    FROM '${directory}/src/main/resources/database/bookcategory.csv'
    DELIMITER ';'
    CSV HEADER;

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
END;\$\$;"

result=$(psql -U bsms -d bsms -c "${sql_command}" 2>&1)


if [ $? -eq 0 ]; then
  echo "Successfully imported book category data."
else
  echo "Import of book category data failed: $result"
fi

# Account
sql_command="DO \$\$BEGIN
  BEGIN
    COPY Account (phone, password, name, gender, email, address, isAdmin, isLocked)
    FROM '${directory}/src/main/resources/database/account.csv'
    DELIMITER ';'
    CSV HEADER;
  EXCEPTION
    WHEN others THEN
      RAISE EXCEPTION 'Failed to copy data: %', SQLERRM;
  END;
END;\$\$;"


result=$(psql -U bsms -d bsms -c "${sql_command}" 2>&1)

if [ $? -eq 0 ]; then
  echo "Successfully imported account data."
else
  echo "Import of account data failed: $result"
fi

# Member
sql_command="DO \$\$BEGIN
  BEGIN
    COPY Member (phone, name, gender, dateOfBirth, email, address)
    FROM '${directory}/src/main/resources/database/member.csv'
    DELIMITER ';'
    CSV HEADER;
  EXCEPTION
    WHEN others THEN
      RAISE EXCEPTION 'Failed to copy data: %', SQLERRM;
  END;
END;\$\$;"

result=$(psql -U bsms -d bsms -c "${sql_command}" 2>&1)

if [ $? -eq 0 ]; then
  echo "Successfully imported member data."
else
  echo "Import of member data failed: $result"
fi

# Import books from importedBook1 
sql_command="DO \$\$BEGIN
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
    CREATE TEMP TABLE temp_imported_books  (
        bookTitle char(255) not null,
        quantity int check (quantity >= 0) not null,
        pricePerBook decimal(12, 2) check (pricePerBook >= 0) not null
    );

    COPY temp_imported_books (bookTitle, quantity, pricePerBook) 
    FROM '${directory}/src/main/resources/database/importedbook1.csv'
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
END;\$\$;"


result=$(psql -U bsms -d bsms -c "${sql_command}" 2>&1)


if [ $? -eq 0 ]; then
  echo "Successfully imported importSheet1 data."
else
  echo "Import of importSheet1 data failed: $result"
fi

# Import books from importedBook2
sql_command="DO \$\$BEGIN
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
    CREATE TEMP TABLE temp_imported_books  (
        bookTitle char(255) not null,
        quantity int check (quantity >= 0) not null,
        pricePerBook decimal(12, 2) check (pricePerBook >= 0) not null
    );

    COPY temp_imported_books (bookTitle, quantity, pricePerBook) 
    FROM '${directory}/src/main/resources/database/importedbook2.csv'
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
END;\$\$;"


result=$(psql -U bsms -d bsms -c "${sql_command}" 2>&1)


if [ $? -eq 0 ]; then
  echo "Successfully imported importSheet2 data."
else
  echo "Import of importSheet2 data failed: $result"
fi

# Import books from importedBook3
sql_command="DO \$\$BEGIN
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
    CREATE TEMP TABLE temp_imported_books  (
        bookTitle char(255) not null,
        quantity int check (quantity >= 0) not null,
        pricePerBook decimal(12, 2) check (pricePerBook >= 0) not null
    );

    COPY temp_imported_books (bookTitle, quantity, pricePerBook) 
    FROM '${directory}/src/main/resources/database/importedbook3.csv'
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
END;\$\$;"


result=$(psql -U bsms -d bsms -c "${sql_command}" 2>&1)


if [ $? -eq 0 ]; then
  echo "Successfully imported importSheet3 data."
else
  echo "Import of importSheet3 data failed: $result"
fi

# Import books from importedBook4
sql_command="DO \$\$BEGIN
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
    CREATE TEMP TABLE temp_imported_books  (
        bookTitle char(255) not null,
        quantity int check (quantity >= 0) not null,
        pricePerBook decimal(12, 2) check (pricePerBook >= 0) not null
    );

    COPY temp_imported_books (bookTitle, quantity, pricePerBook) 
    FROM '${directory}/src/main/resources/database/importedbook4.csv'
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
END;\$\$;"


result=$(psql -U bsms -d bsms -c "${sql_command}" 2>&1)


if [ $? -eq 0 ]; then
  echo "Successfully imported importSheet4 data."
else
  echo "Import of importSheet4 data failed: $result"
fi

# Order 1 Anynomous
sql_command="DO \$\$BEGIN
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
END;\$\$;"


result=$(psql -U bsms -d bsms -c "${sql_command}" 2>&1)


if [ $? -eq 0 ]; then
  echo "Successfully imported order 1 data."
else
  echo "Import of order 1 data failed: $result"
fi

# Import Order 2
sql_command="DO \$\$BEGIN
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
END;\$\$;"


result=$(psql -U bsms -d bsms -c "${sql_command}" 2>&1)


if [ $? -eq 0 ]; then
  echo "Successfully imported order 2 data."
else
  echo "Import of order data 2 failed: $result"
fi

# Import Order 3 Anonymous

sql_command="DO \$\$BEGIN
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
END;\$\$;"


result=$(psql -U bsms -d bsms -c "${sql_command}" 2>&1)


if [ $? -eq 0 ]; then
  echo "Successfully imported order 3 data."
else
  echo "Import of order 3 data failed: $result"
fi


# Import order 4
sql_command="DO \$\$BEGIN
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
END;\$\$;"


result=$(psql -U bsms -d bsms -c "${sql_command}" 2>&1)


if [ $? -eq 0 ]; then
  echo "Successfully imported order 4 data."
else
  echo "Import of order data 4 failed: $result"
fi
