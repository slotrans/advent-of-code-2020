/*
# create table advent2020.input03 ( idx serial, val text ) ; --order matters so we need to add the index at load time

# \copy input03 ( val ) from 'input03' ;

# select * from input03 order by idx limit 20 ;
 idx |               val
-----+---------------------------------
   1 | ....#.#..#.#.#.#......#....##.#
   2 | ..##..#.#..#.##.....#.....#....
   3 | ....#..#...#..#..####.##.#.##..
   4 | ...............#.....##..##..#.
   5 | ##...####..##.#..#...####...#.#
   6 | ..#.#....##....##.........#...#
   7 | .#..#.##..............#.....###
   8 | ##..##..#.....#..#...#....#....
   9 | .#.........#..#...#.#.#.....#..
  10 | ......#...#..#.##..#.....#.#...
  11 | .#...#.#.#.##.##.....###...#...
  12 | ..........#.......#...#....#..#
  13 | .....##..#.#...#...##.##.......
  14 | ...#.###.#.#..##...#.#.........
  15 | ###.###....#...###.#.##...#....
  16 | ...........#....#.....##....###
  17 | #..#.......#.....#.....##....#.
  18 | .##.#....#...#....#......#..##.
  19 | ..#....#..#..#......#..........
  20 | #..#.........#.#....#.##...#.#.

note the field is 31 spaces wide
*/

--start position is 1,1

with var as
(
    select 1 as x_slope
         , 2 as y_slope
)
, zero_based as
(
    select idx-1 as IDX
         , val
      from input03
     where 1=1
     order by 1
)
, adjusted_for_y_slope as
(
    select idx as ORIG_IDX
         , floor(idx / (select y_slope from var))::int as IDX
         , mod(idx, (select y_slope from var)) = 0 as LANDED_THIS_ROW
         , val
      from zero_based
     where 1=1
     order by 1
)
, raw_movement as
(
    select orig_idx
         , case when landed_this_row 
                then idx 
                 end as IDX
         , case when landed_this_row
                --then 1 /* X start */ + (idx * (select x_slope from var)) 
                then 1 /* X start */ + (idx * (select x_slope from var))
                 end as XPOS
         , case when landed_this_row 
                then 1 /* Y start */ + (idx * (select y_slope from var))
                 end as YPOS 
         , val
      from adjusted_for_y_slope
     where 1=1
     order by orig_idx
)
, wrapped_movement as
(
    select orig_idx
         , idx
         , mod(xpos-1, 31)+1 as XPOS
         , ypos
         , val
      from raw_movement
     where 1=1
     order by orig_idx
)
, illustrated as
(
    select orig_idx
         , idx
         , xpos
         , ypos
         , coalesce( overlay(val 
                             placing (case substring(val from xpos for 1) when '.' then 'O' when '#' then 'X' end)
                             from xpos
                             for 1
                            )
                   , val
                   ) as _PATH
      from wrapped_movement
     where 1=1
     order by orig_idx
)
select orig_idx
     , idx
     , xpos
     , ypos
     , _path
     , sum(case when _path like '%X%' then 1 else 0 end) 
       over(order by orig_idx rows between unbounded preceding and current row) as TREE_COUNTER
  from illustrated
 where 1=1
 order by orig_idx
;

--answer: 159


--part 2
/*
Determine the number of trees you would encounter if, for each of the following slopes, 
you start at the top-left corner and traverse the map all the way to the bottom:

    Right 1, down 1.
    Right 3, down 1. (This is the slope you already checked.)
    Right 5, down 1.
    Right 7, down 1.
    Right 1, down 2.

What do you get if you multiply together the number of trees encountered on each of the listed slopes?
*/

-- 1/1 ->  86
-- 3/1 -> 159
-- 5/1 ->  97
-- 7/1 ->  88
-- 1/2 ->  55

--product is larger than signed 32-bit
select 86::bigint * 159::bigint * 97::bigint * 88::bigint * 55::bigint ;

6419669520
