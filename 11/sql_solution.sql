/*
=# create table advent2020.input11 ( idx serial primary key, val text not null ) ;
CREATE TABLE
=# \copy advent2020.input11 ( val ) from 'input11' ;
COPY 98
=# select * from advent2020.input11 order by idx limit 20 ;
 idx |                                                val
-----+----------------------------------------------------------------------------------------------------
   1 | LLLLLL.LLLLLLLLLLLL.LLLLLL.LLLLLLLLLLLL.LLLLLLLLLLLLL.LLLLLLLLLLLLLLLLLLLLLLLLL.LLLLLLLL.LLLLLLLLL
   2 | LLLLLL.LLLLLLLLLLLL.LLLLLLL.LLLLL.LLLLL.LLLLLLLLLLLLLLLLLLLLL.LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL.LL
   3 | LLLLLLLLLLLLL.LLLLLLLLLLLLL.LLLLL.LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL.LLLLLLLL.L.LLLLLL.LL.LLLLLL
   4 | LLLLLLLLLLLLL.LLLLL.LLLLLLLLLLLLLLLLL.LLLLLLLLLL.LLLL.LLLLLLL.LLLLLLLLLLLLLLLLL.LLLLLLLL.LLLLLLLLL
   5 | LLLLL..LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL.LLLLLLLL.LLLL.LLLLLLL.LLLLL.LL.LLLLLLLLLLLLLL.LL.LLLLLLLLL
   6 | LLLLLL.LLLLL..LLLLL.LLLLLLL.LLLLL.LLLLLLLL.LLLLLLLLLLLLLLLLLLLLLLLLLLL.LLLLLLL.LLLLLLLLL.L.LLLLL.L
   7 | LLLLLL.LLLLLL.LLL.L.LLLLLLL.LLLLL.LLLLL.LLLLLLLL.LLLL.LLLLLL..LLLLLLLL.LLLLLLLL.LLLLLLLLLLLLLL.LLL
   8 | .LLLLLLLLLLLL.LLL.L.LLLLLLLLLLLLLLLLLLL.LLLLLLLL.LLLLLLLLLLLL.LLLLLL.L.L.LLLLLLLLLLLLL.L.LLLLLLLLL
   9 | LLLLLL.LLLLLL.LLLLL.LLLLLLL..LLLLLLLLLL..LLLLLLL.LLLLLLLLLLLL.LLLLLLLL.LLLLLLLL.LLL.LLLL..LLLLLLLL
  10 | ..........LL....L..LL..L.....L...L....L............L.LL...L.......L.L.LL...L.L...L....L.L....LLL..
  11 | LLLLLL.LLLLLL.LLLLL.LLLLLLL.LLL.L.LLLLL.LLLLLLLLLLLLLLLLLLLLL.LLLLLLLLLLLLL.LL..LLLLLLL.LLLLLLLLLL
  12 | LLLLLLLLLLLLL.LLLLL.LLLLLLL.LLL.L.LLLLLLLLLLLLLL.LLLL.LLLLLLL.LLLLLLLL.LLLLLLLL.LLLLLLLLLLLLLL.LLL
  13 | .LLLLL..LL.LL..LLL.LLLLLLLL.LLLLL.LLLLL.LLLLLLLLLLLLL.LLLLLLL.LLLLLLLLLLLLLLLLL.LLLLLLLLLLLLLLLLLL
  14 | L.LLLL.LLL.LLLLLLLL.LLLLLLL.LLLLL.LLLLLLLLLLLLLLLLLLL.LLL.LLL.LLLLLLLL.LLLLLLLL.LLLLLLLLLLLLL.LLLL
  15 | LLLLLL.LLLLLLLLLLLLLLLLLLLL.L.LLLLLLLLL.LLLLLLLLLLLLLLLLLLLLL.LLLLLL.L.LLLLLLLL.LLLLLLLL.LLLLLLLLL
  16 | LLLLLL.LLLLLLLLLLLL.LLLLLLLLLLLLL.LLLLL.LLLLLLLL.LLLL.LLLLLLL.LLLLLL.L.LLLLLLLL.LLLLLLLL.LLLLLLLLL
  17 | ...LLLLL.......L.LL..L..LL...LL..L.....L...L...LLL.L.L.....L.L....L.L......LL..L.L.L..LL.L.....LL.
  18 | LLLLLLLLLLL.L.LLL.LLLLLLLLL.LLLLLLLLLLL.LLLLLLLL.LLLL.LLLLLLLLLLLLLLLL.LLLLLL.L.LL.LLLLLLLLLLLLLLL
  19 | LLLLLL.LLLLLL.LLLLL.LLLLLLL.LLLLL.LLLLL.LLLLLLLLLLLLL.LLL.LLL.LLLLLLLL.LLLLLLLL..LLLLLLLLLLLLLL.LL
  20 | LLLLLL.LLLLLLLLLLLL.LLLLLLL.LLLLL.LLLLLLLLLLLLLLLLLLL.LLLLL.L.LLLLLLLLLLLLLLLLL.LLLLLLLL.LLLLLLLLL
(20 rows)

=# select * from advent2020_samples.input11 order by idx ;
 idx |    val
-----+------------
   1 | L.LL.LL.LL
   2 | LLLLLLL.LL
   3 | L.L.L..L..
   4 | LLLL.LL.LL
   5 | L.LL.LL.LL
   6 | L.LLLLL.LL
   7 | ..L.L.....
   8 | LLLLLLLLLL
   9 | L.LLLLLL.L
  10 | L.LLLLL.LL
*/

with input as
(
    select * from advent2020_samples.input11
)
, exploded as
(
    select i.idx as ROW
         , v.col
         , v.seat
      from input i
         , lateral unnest(string_to_array(i.val, null)) with ordinality v(seat, col)
     where 1=1
)
select a.row
     , a.col
     , a.seat
     , (select case when a.seat = 'L'
                     and count(case when b.seat = '#' then 1 end) = 0
                    then '#'
                    when a.seat = '#'
                     and count(case when b.seat = '#' then 1 end) >= 4
                    then 'L'
                    else a.seat
                     end
          from exploded b
         where 1=1
           and b.row between a.row-1 and a.row+1
           and b.col between a.col-1 and a.col+1
           and (b.row, b.col) != (a.row, a.col)
       ) as NEW_VAL
  from exploded a
 where 1=1
;