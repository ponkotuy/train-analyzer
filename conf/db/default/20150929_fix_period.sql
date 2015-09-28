
ALTER TABLE line DROP time_table_period;

ALTER TABLE pattern ADD time_table_period int not null default 60;
