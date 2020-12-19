/*
=# create table advent2020.input10 ( idx serial primary key, val int not null ) ;
CREATE TABLE
=# \copy advent2020.input10 (val) from 'input10' ;
COPY 94
=# select * from advent2020.input10 limit 10 ;
 idx | val
-----+-----
   1 |  26
   2 |  97
   3 |  31
   4 |   7
   5 |   2
   6 |  10
   7 |  46
   8 |  38
   9 | 112
  10 |  54
(10 rows)
*/

with extended as
(
    select 0 as joltage
    union all
    (
    select val
      from advent2020.input10
     where 1=1
     order by val
    )
    union all
    select max(val)+3
      from advent2020.input10
     where 1=1
)
, labeled as
(
    select joltage
         , row_number() over(order by joltage) as IDX
      from extended
     where 1=1
)
, differences as
(
    select joltage
         , joltage - lag(joltage,1) over(order by joltage) as DIFFERENCE
      from labeled
     where 1=1
)
, diff_counts as
(
    select difference
         , count(1) as _COUNT
      from differences
     where 1=1
     group by difference
)
, part1_answer as
(
    select 'part 1'
         , (select _count from diff_counts where difference = 1) * (select _count from diff_counts where difference = 3) as ANSWER
    --answer: 1914
)
select * from part1_answer
;
