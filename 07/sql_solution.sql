/*
=# create table advent2020.input07 ( idx serial primary key, val text not null ) ;
CREATE TABLE
=# \copy input07 (val) from 'input07' ;
COPY 594
Time: 56.371 ms
=# select * from input07 order by idx limit 20 ;
 idx |                                                      val
-----+---------------------------------------------------------------------------------------------------------------
   1 | striped beige bags contain 5 dull beige bags.
   2 | dark turquoise bags contain 4 dark bronze bags, 3 posh tan bags.
   3 | mirrored turquoise bags contain 2 dim crimson bags, 4 clear crimson bags, 1 dotted blue bag.
   4 | striped gray bags contain 4 muted violet bags, 1 clear fuchsia bag, 2 dull violet bags.
   5 | bright cyan bags contain 4 dim aqua bags, 4 vibrant salmon bags.
   6 | dark aqua bags contain 4 dim brown bags.
   7 | dull magenta bags contain 5 dim plum bags, 5 dark coral bags, 3 mirrored white bags, 3 posh teal bags.
   8 | dull silver bags contain 4 dull crimson bags.
   9 | dull blue bags contain 1 shiny violet bag, 4 plaid magenta bags, 4 dull green bags.
  10 | muted white bags contain 4 dark aqua bags, 4 pale red bags, 1 striped maroon bag, 1 striped tomato bag.
  11 | shiny gray bags contain 3 dim chartreuse bags, 4 muted turquoise bags.
  12 | posh yellow bags contain 4 dim cyan bags.
  13 | wavy tan bags contain 2 plaid beige bags.
  14 | posh maroon bags contain 5 mirrored blue bags.
  15 | faded red bags contain 3 drab red bags.
  16 | striped cyan bags contain 1 bright black bag, 4 posh cyan bags, 2 striped purple bags, 1 pale fuchsia bag.
  17 | faded teal bags contain 4 vibrant gray bags, 3 dim black bags, 5 muted lime bags, 4 striped aqua bags.
  18 | striped gold bags contain 1 dotted blue bag, 5 drab bronze bags, 2 mirrored orange bags, 2 shiny violet bags.
  19 | dull cyan bags contain 5 posh teal bags, 3 pale chartreuse bags.
  20 | clear beige bags contain 3 mirrored white bags, 4 dotted orange bags, 4 muted purple bags.
(20 rows)
*/

with first_split as
(
    select split_part(val, ' bags contain ', 1) as OUTER_COLOR
         , split_part(val, ' bags contain ', 2) as INNER_PART
      from advent2020.input07
     where 1=1
)
, color as --table of unique color names
(
    select outer_color as COLOR_KEY
      from first_split
     where 1=1
)
, exploded as
(
    select outer_color
         , regexp_matches(inner_part, '([0-9]+)\s([a-z\s]+) bag(s)?', 'g') as CONTENT_RULE
      from first_split
     where 1=1
)
, rules as
(
    select outer_color as OUTER_COLOR_KEY
         , content_rule[1] as INNER_COUNT
         , content_rule[2] as INNER_COLOR_KEY
      from exploded
     where 1=1
)
, part1_base as
(
    --this is not efficient
    with recursive rule_expansion(outer_color_key, inner_color_arr) as
    (
        select outer_color_key
             , array[inner_color_key] as INNER_COLOR_ARR
          from rules
         where 1=1
        union all
        select exp.outer_color_key
             --, array_prepend(r.inner_color_key, exp.inner_color_arr) as INNER_COLOR_ARR
             , array_append(exp.inner_color_arr, r.inner_color_key) as INNER_COLOR_ARR
          from rule_expansion exp
          --join rules r on(exp.inner_color_arr[1] = r.outer_color_key)
          join rules r on(exp.inner_color_arr[array_length(exp.inner_color_arr, 1)] = r.outer_color_key)
         where 1=1
    )
    select * from rule_expansion
)
, part1_answer as
(
    --also not efficient
    select 'part 1'
         , count(distinct outer_color_key)
      from part1_base
     where 1=1
       and (',' || array_to_string(inner_color_arr, ',') || ',') like '%,shiny gold,%'
    --answer: 101
)
select * from part1_answer
;
