-- Instruction
-- 0. If there is existing database, please delete and create a new one
-- Please also check if psql exists in your PATH by 'psql --help'. If not, please install and put it in your path
-- 1. Create database named bsms with user bsms
-- 2. In the bsms.properties, add one line repoDirectory=[repoDirectory], with [repoDirectory] being the directory of the folder
-- e.g. repoDirectory=/Users/tranchau/Project/JAVA/Bookstore Management System/Java-21vp-group6/
-- 3. Run the shell script init.sh by doing ./[file] 
-- e.g. ./src/main/resources/database/init.sh
-- You don't need to run this file at all. The shell script has already included command to run this file
-- 4. The results displayed in the console should be all successsful. If not, please ask
-- The results of the select count(*) for each table:
-- 418 book
-- 32 author
-- 25 publisher
-- 75 category
-- 730 book category
-- 4 importsheet
-- 76 importedbook
-- 1001 member
-- 12 account, 3 of which admin
-- 4 ordersheet
-- 12 orderedbook


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
    hiddenParentCount int check (hiddenParentCount >= 0) not null
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