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

-- Data
insert into Account (phone, password, name, gender, email, address, isAdmin, isLocked) values
    ('1234567890', 'password123', 'John Doe', 'Male', 'john@example.com', '123 Main St', true, false),
    ('9876543210', 'securepass', 'Jane Doe', 'Female', 'jane@example.com', '456 Oak Ave', false, true);

insert into Author (name, overview, isHidden) values
    ('Author X', 'Experienced writer.', false),
    ('Author Y', 'New talent.', true);

insert into Publisher (name, email, address, isHidden) values
    ('Publisher A', 'publisherA@example.com', '123 Main St', false),
    ('Publisher B', 'publisherB@example.com', '456 Oak Ave', true);

insert into Book (
    title, authorId, publisherId, pageCount, dimension, translatorName,
    overview, quantity, salePrice, hiddenParentCount, publishdate, maxImportPrice
) values
    ('Sample Book 1', 1, 1, 300, '6x9x1 cm', 'Translator A', 'An interesting book.', 50, 19.99, 5, '1990-05-25', 20.50),
    ('Sample Book 2', 2, 2, 250, '5x8x1 cm', 'Translator B', 'A captivating novel.', 30, 24.99, 8, '1995-05-15', 18.50),
    ('Sample Book 3', 2, 2, 250, '5x8x1 cm', 'Translator B', 'A captivating novel.', 30, 24.99, 8, '1995-05-15', 18.50),
    ('Sample Book 4', 1, 1, 300, '6x9x1 cm', 'Translator A', 'An interesting book.', 50, 19.99, 5, '1990-05-25', 20.50),
    ('Sample Book 5', 2, 2, 250, '5x8x1 cm', 'Translator B', 'A captivating novel.', 30, 24.99, 0, '1995-05-15', 18.50),
    ('Sample Book 6', 2, 2, 250, '5x8x1 cm', 'Translator B', 'A captivating novel.', 30, 24.99, 8, '1995-05-15', 18.50),
    ('Sample Book 7', 1, 1, 300, '6x9x1 cm', 'Translator A', 'An interesting book.', 50, 19.99, 5, '1990-05-25', 20.50),
    ('Sample Book 8', 2, 2, 250, '5x8x1 cm', 'Translator B', 'A captivating novel.', 30, 24.99, 8, '1995-05-15', 18.50),
    ('Sample Book 9', 2, 2, 250, '5x8x1 cm', 'Translator B', 'A captivating novel.', 30, 24.99, 0, '1995-05-15', 18.50),
    ('Sample Book 10', 1, 1, 300, '6x9x1 cm', 'Translator A', 'An interesting book.', 50, 19.99, 5, '1990-05-25', 20.50),
    ('Sample Book 12', 2, 2, 250, '5x8x1 cm', 'Translator B', 'A captivating novel.', 30, 24.99, 8, '1995-05-15', 18.50),
    ('Sample Book 13', 2, 2, 250, '5x8x1 cm', 'Translator B', 'A captivating novel.', 30, 24.99, 8, '1995-05-15', 18.50),
    ('Sample Book 14', 1, 1, 300, '6x9x1 cm', 'Translator A', 'An interesting book.', 50, 19.99, 0, '1990-05-25', 20.50),
    ('Sample Book 15', 2, 2, 250, '5x8x1 cm', 'Translator B', 'A captivating novel.', 30, 24.99, 8, '1995-05-15', 18.50),
    ('Sample Book 16', 2, 2, 250, '5x8x1 cm', 'Translator B', 'A captivating novel.', 30, 24.99, 0, '1995-05-15', 18.50),
    ('Sample Book 17', 1, 1, 300, '6x9x1 cm', 'Translator A', 'An interesting book.', 50, 19.99, 5, '1990-05-25', 20.50),
    ('Sample Book 18', 2, 2, 250, '5x8x1 cm', 'Translator B', 'A captivating novel.', 30, 24.99, 8, '1995-05-15', 18.50),
    ('Sample Book 19', 2, 2, 250, '5x8x1 cm', 'Translator B', 'A captivating novel.', 30, 24.99, 8, '1995-05-15', 18.50),
    ('Sample Book 20', 1, 1, 300, '6x9x1 cm', 'Translator A', 'An interesting book.', 50, 19.99, 5, '1990-05-25', 20.50),
    ('Sample Book 21', 2, 2, 250, '5x8x1 cm', 'Translator B', 'A captivating novel.', 30, 24.99, 8, '1995-05-15', 18.50),
    ('Sample Book 22', 2, 2, 250, '5x8x1 cm', 'Translator B', 'A captivating novel.', 30, 24.99, 8, '1995-05-15', 18.50),
    ('Sample Book 23', 1, 1, 300, '6x9x1 cm', 'Translator A', 'An interesting book.', 50, 19.99, 5, '1990-05-25', 20.50),
    ('Sample Book 24', 2, 2, 250, '5x8x1 cm', 'Translator B', 'A captivating novel.', 30, 24.99, 0, '1995-05-15', 18.50),
    ('Sample Book 25', 2, 2, 250, '5x8x1 cm', 'Translator B', 'A captivating novel.', 30, 24.99, 8, '1995-05-15', 18.50),
    ('Sample Book 26', 1, 1, 300, '6x9x1 cm', 'Translator A', 'An interesting book.', 50, 19.99, 5, '1990-05-25', 20.50),
    ('Sample Book 27', 2, 2, 250, '5x8x1 cm', 'Translator B', 'A captivating novel.', 30, 24.99, 8, '1995-05-15', 18.50),
    ('Sample Book 28', 2, 2, 250, '5x8x1 cm', 'Translator B', 'A captivating novel.', 30, 24.99, 0, '1995-05-15', 18.50),
    ('Sample Book 29', 1, 1, 300, '6x9x1 cm', 'Translator A', 'An interesting book.', 50, 19.99, 5, '1990-05-25', 20.50),
    ('Sample Book 30', 2, 2, 250, '5x8x1 cm', 'Translator B', 'A captivating novel.', 30, 24.99, 8, '1995-05-15', 18.50),
    ('Sample Book 31', 2, 2, 250, '5x8x1 cm', 'Translator B', 'A captivating novel.', 30, 24.99, 8, '1995-05-15', 18.50),
    ('Sample Book 32', 1, 1, 300, '6x9x1 cm', 'Translator A', 'An interesting book.', 50, 19.99, 0, '1990-05-25', 20.50),
    ('Sample Book 33', 2, 2, 250, '5x8x1 cm', 'Translator B', 'A captivating novel.', 30, 24.99, 8, '1995-05-15', 18.50),
    ('Sample Book 34', 2, 2, 250, '5x8x1 cm', 'Translator B', 'A captivating novel.', 30, 24.99, 0, '1995-05-15', 18.50),
    ('Sample Book 35', 1, 1, 300, '6x9x1 cm', 'Translator A', 'An interesting book.', 50, 19.99, 5, '1990-05-25', 20.50),
    ('Sample Book 36', 2, 2, 250, '5x8x1 cm', 'Translator B', 'A captivating novel.', 30, 24.99, 8, '1995-05-15', 18.50),
    ('Sample Book 37', 2, 2, 250, '5x8x1 cm', 'Translator B', 'A captivating novel.', 30, 24.99, 8, '1995-05-15', 18.50),
    ('Sample Book 38', 2, 2, 250, '5x8x1 cm', 'Translator B', 'A captivating novel.', 30, 24.99, 8, '1995-05-15', 18.50),
    ('Sample Book 39', 1, 1, 300, '6x9x1 cm', 'Translator A', 'An interesting book.', 50, 19.99, 5, '1990-05-25', 20.50),
    ('Sample Book 40', 2, 2, 250, '5x8x1 cm', 'Translator B', 'A captivating novel.', 30, 24.99, 0, '1995-05-15', 18.50),
    ('Sample Book 41', 2, 2, 250, '5x8x1 cm', 'Translator B', 'A captivating novel.', 30, 24.99, 8, '1995-05-15', 18.50),
    ('Sample Book 42', 1, 1, 300, '6x9x1 cm', 'Translator A', 'An interesting book.', 50, 19.99, 5, '1990-05-25', 20.50),
    ('Sample Book 43', 2, 2, 250, '5x8x1 cm', 'Translator B', 'A captivating novel.', 30, 24.99, 8, '1995-05-15', 18.50),
    ('Sample Book 44', 2, 2, 250, '5x8x1 cm', 'Translator B', 'A captivating novel.', 30, 24.99, 0, '1995-05-15', 18.50),
    ('Sample Book 45', 1, 1, 300, '6x9x1 cm', 'Translator A', 'An interesting book.', 50, 19.99, 5, '1990-05-25', 20.50),
    ('Sample Book 46', 2, 2, 250, '5x8x1 cm', 'Translator B', 'A captivating novel.', 30, 24.99, 8, '1995-05-15', 18.50),
    ('Sample Book 47', 2, 2, 250, '5x8x1 cm', 'Translator B', 'A captivating novel.', 30, 24.99, 8, '1995-05-15', 18.50),
    ('Sample Book 48', 1, 1, 300, '6x9x1 cm', 'Translator A', 'An interesting book.', 50, 19.99, 0, '1990-05-25', 20.50),
    ('Sample Book 49', 2, 2, 250, '5x8x1 cm', 'Translator B', 'A captivating novel.', 30, 24.99, 8, '1995-05-15', 18.50),
    ('Sample Book 50', 2, 2, 250, '5x8x1 cm', 'Translator B', 'A captivating novel.', 30, 24.99, 0, '1995-05-15', 18.50),
    ('Sample Book 51', 1, 1, 300, '6x9x1 cm', 'Translator A', 'An interesting book.', 50, 19.99, 5, '1990-05-25', 20.50),
    ('Sample Book 52', 2, 2, 250, '5x8x1 cm', 'Translator B', 'A captivating novel.', 30, 24.99, 8, '1995-05-15', 18.50),
    ('Sample Book 53', 2, 2, 250, '5x8x1 cm', 'Translator B', 'A captivating novel.', 30, 24.99, 8, '1995-05-15', 18.50);


insert into Category (name, isHidden) values
    ('Fiction', false),
    ('Non-Fiction', false);

insert into BookCategory (bookId, categoryId) values
    (1, 1),
    (2, 2),
    (1, 2);

insert into ImportSheet (employeeInChargeId, importDate, totalCost) values
    (1, current_date, 150.99),
    (2, current_date, 200.50);

insert into ImportedBook (importSheetId, bookId, quantity, pricePerBook) values
    (1, 1, 10, 15.99),
    (1, 2, 8, 20.50),
    (2, 1, 5, 12.99),
    (2, 2, 3, 18.50);

insert into Member (phone, name, gender, dateOfBirth, email, address) values
    ('1112233444', 'Alice', 'Female', '1990-05-15', 'alice@example.com', '789 Elm St'),
    ('5556677888', 'Bob', 'Male', '1985-08-22', 'bob@example.com', '456 Pine Ave');

insert into OrderSheet (memberId, employeeInChargeId, orderDate, discountedTotalCost) values
    (1, 1, current_date, 75.99),
    (2, 2, current_date, 120.25);

insert into OrderedBook (orderSheetId, bookId, quantity, pricePerBook) values
    (1, 1, 5, 15.99),
    (1, 2, 3, 20.50),
    (2, 1, 7, 12.99),
    (2, 2, 5, 18.50);