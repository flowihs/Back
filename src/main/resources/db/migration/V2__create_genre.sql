create table if not exists bookcrossing.t_genre(
    id integer primary key,
    ru_name varchar(45),
    eng_name varchar(45)
);
comment on table bookcrossing.t_genre is 'Directory of book genres';

insert into bookcrossing.t_genre(id, ru_name, eng_name) values
(0, 'Другое', 'Other'),
(1, 'Роман', 'Novel'),
(2, 'Повесть', 'Short novel'),
(3, 'Рассказ', 'Story'),
(4, 'Поэзия', 'Poetry'),
(5, 'Драма', 'Drama'),
(6, 'Трагедия', 'Tragedy'),
(7, 'Комедия', 'Comedy'),
(8, 'Эссе', 'Essay'),
(9, 'Научная фантастика', 'Science fiction'),
(10, 'Фэнтези', 'Fantasy'),
(11, 'Детектив', 'Detective'),
(12, 'Криминальный роман', 'Crime novel'),
(13, 'Исторический роман', 'Historical novel'),
(14, 'Приключенческий роман', 'Adventure novel'),
(15, 'Ужасы', 'Horror'),
(16, 'Триллер', 'Thriller'),
(17, 'Мелодрама', 'Melodrama'),
(18, 'Психологический триллер', 'Psychological thriller'),
(19, 'Любовный роман', 'Romance novel'),
(20, 'Автобиография', 'Autobiography'),
(21, 'Биографический роман', 'Biographical novel'),
(22, 'Документальная проза', 'Documentary prose'),
(23, 'Философский трактат', 'Philosophical treatise'),
(24, 'Путевые заметки', 'Travel notes'),
(25, 'Мемуары', 'Memoirs'),
(26, 'Справочная литература', 'Reference literature'),
(27, 'Энциклопедия', 'Encyclopedia'),
(28, 'Учебник', 'Textbook'),
(29, 'Научно-популярная книга', 'Popular science book'),
(30, 'Самоучитель', 'Self-instruction manual'),
(31, 'Бизнес-литература', 'Business literature'),
(32, 'Публицистика', 'Journalistic literature'),
(33, 'Критическая статья', 'Critical article'),
(34, 'Религиозная литература', 'Religious literature'),
(35, 'Мифология', 'Mythology'),
(36, 'Сказка', 'Fairy tale'),
(37, 'Басня', 'Fable'),
(38, 'Альманах', 'Almanac'),
(39, 'Сборник стихов', 'Collection of poems'),
(40, 'Монография', 'Monograph'),
(41, 'Очерк', 'Sketch'),
(42, 'Новелла', 'Novelette'),
(43, 'Пьеса', 'Play'),
(44, 'Лирика', 'Lyric poetry'),
(45, 'Сонет', 'Sonnet'),
(46, 'Баллада', 'Ballad'),
(47, 'Одиссея', 'Odyssey'),
(48, 'Эпос', 'Epic poem'),
(49, 'Трактат', 'Treatise'),
(50, 'Диалог', 'Dialogue'),
(51, 'Пародия', 'Parody'),
(52, 'Сатира', 'Satire'),
(53, 'Юмористический рассказ', 'Humorous story'),
(54, 'Антиутопия', 'Dystopian novel'),
(55, 'Постапокалипсис', 'Post-apocalyptic novel'),
(56, 'Космическая опера', 'Space opera'),
(57, 'Магический реализм', 'Magical realism'),
(58, 'Героическое фэнтези', 'Heroic fantasy'),
(59, 'Молодежная литература', 'Young adult literature'),
(60, 'Детская литература', 'Childrens literature'),
(61, 'Графический роман', 'Graphic novel'),
(62, 'Манга', 'Manga'),
(63, 'Комикс', 'Comic book'),
(64, 'Микрорассказ', 'Flash fiction'),
(65, 'Заметки', 'Notes'),
(66, 'Интервью', 'Interview'),
(67, 'Рецензия', 'Review'),
(68, 'Справочник', 'Handbook'),
(69, 'Руководство', 'Guide'),
(70, 'Словарь', 'Dictionary'),
(71, 'Атлас', 'Atlas'),
(72, 'Каталог', 'Catalogue'),
(73, 'Газета', 'Newspaper'),
(74, 'Журнал', 'Magazine'),
(75, 'Ежедневник', 'Diary'),
(76, 'Записки путешественника', 'Travelers notes'),
(77, 'Письмо', 'Letter'),
(78, 'Открытка', 'Postcard'),
(79, 'Обзор', 'Overview'),
(80, 'Статья', 'Article'),
(81, 'Стихотворение', 'Poem'),
(82, 'Рассказ', 'Tale'),
(83, 'Нон-фикшн', 'Non-fiction'),
(84, 'Мистика', 'Mystery'),
(85, 'Шпионский роман', 'Spy novel'),
(86, 'Военная проза', 'War fiction'),
(87, 'Политический роман', 'Political novel'),
(88, 'Социальная драма', 'Social drama'),
(89, 'Экзистенциальная проза', 'Existentialist fiction'),
(90, 'Экспериментальный роман', 'Experimental novel'),
(91, 'Постмодернизм', 'Postmodernism'),
(92, 'Метароман', 'Metafiction'),
(93, 'Эротическая литература', 'Erotic literature'),
(94, 'Вампирский роман', 'Vampire novel'),
(95, 'Городское фэнтези', 'Urban fantasy'),
(96, 'Паранормальное фэнтези', 'Paranormal fantasy'),
(97, 'Медиа-викторина', 'Media quiz'),
(98, 'Популярная психология', 'Popular psychology'),
(99, 'Популярная философия', 'Popular philosophy'),
(100, 'Популярная история', 'Popular history');

alter table if exists bookcrossing.t_book add column genre_id integer default 0 not null;
update bookcrossing.t_book set genre_id = g.id from bookcrossing.t_genre as g where g.ru_name = genre or g.eng_name = genre;
alter table if exists bookcrossing.t_book drop column genre;

alter table if exists bookcrossing.t_book
    add constraint book_foreign_genre
        foreign key (genre_id) references bookcrossing.t_genre (id);
create index if not exists book_foreign_genre on bookcrossing.t_book (genre_id);