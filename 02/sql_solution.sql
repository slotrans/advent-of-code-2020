--advent2020.input02 ( val text )
/*
16-18 h: hhhhhhhhhhhhhhhhhh
17-18 d: ddddddddddddddddzn
15-18 c: cccccccccccccczcczc
3-9 r: pplzctdrc
4-14 d: lxdmddfddddddd
*/

with parsed as
(
    select split_part(split_part(split_part(val, ':', 1), ' ', 1), '-', 1)::int as LOWER_BOUND
         , split_part(split_part(split_part(val, ':', 1), ' ', 1), '-', 2)::int as UPPER_BOUND
         , split_part(split_part(val, ':', 1), ' ', 2) as LETTER
         , split_part(val, ': ', 2) as PASSWORD
      from advent2020.input02
     where 1=1
)
, analyzed as
(
    select p.password
         , p.letter
         , p.lower_bound
         , p.upper_bound
         , (select count(1)
              from regexp_matches(p.password, p.letter, 'g')
           ) as OCCURRENCES
         , (substring(p.password from p.lower_bound for 1) = p.letter) as AT_FIRST_POSITION
         , (substring(p.password from p.upper_bound for 1) = p.letter) as AT_SECOND_POSITION
      from parsed p
     where 1=1
)
, part1 as
(
    select 'part 1'
         , count(1)
      from analyzed
     where 1=1
       and occurrences between lower_bound
                           and upper_bound
)
, part2 as
(
    select 'part 2'
         , count(1)
      from analyzed
     where 1=1
       --and (at_first_position::int + at_second_position::int) = 1  --works, but alternative below is shorter
       and (at_first_position != at_second_position) --XOR
)
select * from part1 --465
union all
select * from part2 --294
;
