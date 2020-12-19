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
    select 0 as JOLTAGE
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
, filled as
(
    select series.val as STEP
         , e.joltage
      from generate_series( (select min(joltage) from extended)
                          , (select max(joltage) from extended)
                          ) as series(val)
      left join extended e on(series.val = e.joltage)
     where 1=1
)
, part2_base as 
(
    --the "normal language" solutions accumulate all adapter->ways pairs in a map, whereas this carries through 
    --only the needed pairs, in columns
    with recursive stairs(step, joltage, ways, ways_lag1, ways_lag2) as
    (
        select 0 as STEP
             , 0 as JOLTAGE
             , 1::bigint as WAYS
             , 0::bigint as WAYS_LAG1
             , 0::bigint as WAYS_LAG2
        --
        union all
        --
        select f.step
             , f.joltage
             , case when f.joltage is not null
                    --then (s.ways_lag1 + s.ways_lag2 + s.ways_lag3) 
                    then (s.ways + s.ways_lag1 + s.ways_lag2)
                    else 0::bigint
                     end as WAYS
             , s.ways as WAYS_LAG1
             , s.ways_lag1 as WAYS_LAG2
          from stairs s 
          join filled f on(s.step + 1 = f.step)
         where 1=1
    )
    select * from stairs
)
, part2_answer as
(
    select 'part 2'
         , max(ways) 
      from part2_base
     where 1=1
    --answer: 9256148959232
)
select * from part1_answer
union all
select * from part2_answer
;
