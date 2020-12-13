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
)
select * from part1_answer
;
