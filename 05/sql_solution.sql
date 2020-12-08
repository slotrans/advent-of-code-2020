/*
=# create table advent2020.input05 ( val text not null ) ;
CREATE TABLE
=# \copy advent2020.input05 from 'input05' ;
COPY 839
postgres=# select * from advent2020.input05 limit 20 ;
    val
------------
 BFFBFBFLRL
 BFBFBBBLRR
 BFBFBFBLRR
 BFBFFFFRLR
 BBFFBBFRRR
 FBBBFFFRLL
 FFBFBFFLLL
 BBFBFFFRLL
 FBBBFFBLRR
 FFFFBFFRRL
 BFBFBFBRLL
 FFBFFBBLLL
 BFFBBFFRLR
 FBBFFFFLRR
 FBFBFFBRRR
 FFBFFBBRRR
 FBBBFFFRRL
 FFBBBBFRRL
 BFFBBBBLLL
 BFBBFBBLLR
*/

-- F=0, B=1
-- L=0, R=1
-- after working through this a while I realized "row*8 + col" just treats the whole thing as a 10-bit number,
-- so we can be more direct


with translated as
(
    select val as BOARDING_PASS
         , translate(val, 'FBLR', '0101') as TO_BINARY
      from advent2020.input05
     where 1=1
)
, decoded as
(
    select boarding_pass
         , to_binary
         , to_binary::bit(10)::int as SEAT_ID
      from translated
     where 1=1
)
, part1_answer as
(
    select 'part 1'
         , max(seat_id) as MAX_SEAT_ID
      from decoded
     where 1=1
    --answer: 850
)
, matchup as
(
    select possible.seat_id as POSSIBLE_SEAT_ID
         , decoded.seat_id as TAKEN_SEAT_ID
         , ( lag(decoded.seat_id, 1) over(order by possible.seat_id rows between unbounded preceding and unbounded following) is not null) as IS_PREV_SEAT_TAKEN
         , (lead(decoded.seat_id, 1) over(order by possible.seat_id rows between unbounded preceding and unbounded following) is not null) as IS_NEXT_SEAT_TAKEN
      from generate_series(0, 1024) as possible(seat_id)
      left join decoded on(possible.seat_id = decoded.seat_id)
     where 1=1
)
, part2_answer as
(
    select 'part 2'
         , possible_seat_id as MY_SEAT_ID
      from matchup
     where 1=1
       and taken_seat_id is null
       and is_prev_seat_taken = true
       and is_next_seat_taken = true
)
select * from part1_answer
union all
select * from part2_answer
;
