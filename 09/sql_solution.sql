/*
=# create table advent2020.input09 ( idx serial primary key, val bigint not null );
CREATE TABLE
Time: 7.825 ms
=# \copy advent2020.input09( val ) from 'input09' ;
COPY 1000
Time: 35.365 ms
=# select * from advent2020.input09 order by idx limit 20 ;
 idx | val
-----+-----
   1 |  12
   2 |   6
   3 |  33
   4 |   7
   5 |  21
   6 |  32
   7 |  30
   8 |  37
   9 |  26
  10 |  34
  11 |  18
  12 |   3
  13 |  49
  14 |   1
  15 |  11
  16 |  24
  17 |  15
  18 |  16
  19 |   4
  20 |  20
(20 rows)
*/

with validity_check as
(
    select i.idx
         , i.val
         , exists (select 1
                     from advent2020.input09 x
                     join advent2020.input09 y on(x.val != y.val)
                    where 1=1
                      and x.idx between i.idx - 26 and i.idx - 1
                      and y.idx between i.idx - 26 and i.idx - 1
                      and i.val = x.val + y.val
                  ) as IS_VALID
      from advent2020.input09 i
     where 1=1
       and i.idx > 25
)
, part1_answer as
(
    select 'part 1'
         , val
      from validity_check
     where 1=1
       and is_valid = false
     order by idx
     limit 1
    --answer: 258585477 (idx=594)
)
select * from part1_answer
;


with var as
(
    select 258585477 as TARGET_VAL
         , 594 as TARGET_IDX
)
, input_subset as
(
    select i.idx
         , i.val
      from advent2020.input09 i
     where 1=1
       and i.idx < (select target_idx from var)
)
, part2_base as
(
    select i.idx
         , i.val
         , range.idx as R_IDX
         , range.r_sum
         , range.r_min
         , range.r_max
         , range.r_array
      from input_subset i
      join lateral (select j.idx
                         , sum(j.val) over(order by j.idx rows between unbounded preceding and current row) as R_SUM
                         , array_agg(j.val) over(order by j.idx rows between unbounded preceding and current row) as R_ARRAY
                         , min(j.val) over(order by j.idx rows between unbounded preceding and current row) as R_MIN
                         , max(j.val) over(order by j.idx rows between unbounded preceding and current row) as R_MAX
                      from input_subset j
                     where 1=1
                       and j.idx >= i.idx
                   ) range on(range.r_sum = (select target_val from var))
     where 1=1
       and i.idx >= 2 --need at least 2 in range
     order by i.idx
     limit 1
)
select 'part2'
     , r_min + r_max
  from part2_base
 where 1=1
--answer range {13858643,9455395,9908827,16794010,13221299,11563238,12646458,11137204,11774548,12220424,14302571,14304519,14748447,25865809,22680253,16578014,27525818}
--min 9455395 + max 27525818
--answer: 36981213
;
