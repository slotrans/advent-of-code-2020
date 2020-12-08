/*
postgres=# create table advent2020.input06 ( idx serial primary key, val text not null ) ;
CREATE TABLE
postgres=# \copy advent2020.input06 (val) from 'input06' ;
COPY 2246
postgres=# select * from advent2020.input06 order by idx limit 20 ;
 idx |           val
-----+-------------------------
   1 | lfnghcsvpyrdjtxozimb
   2 | mdtbnorpfalcijxvhsy
   3 | elmwjkfbihydxcpqtovsrun
   4 | tlhmsdjingyxcbfrvpo
   5 |
   6 | a
   7 | a
   8 | xqh
   9 |
  10 | mxdeqcinvfg
  11 | vbncrgzxqefka
  12 |
  13 | oejsdfwm
  14 | fojsmewd
  15 | ewxfsouimdj
  16 | eodafjwsm
  17 | edjwsmfo
  18 |
  19 | d
  20 | d
(20 rows)
*/

--same record-assembly technique from puzzle #4
with marked as
(
    select i.idx
         , i.val
         , (select min(j.idx)
              from advent2020.input06 j
             where i.idx < j.idx
               and j.val = ''
           ) as NEXT_BLANK_LINE
      from advent2020.input06 i
     where 1=1
)
, assembled as
(
    select next_blank_line
         , min(idx) as MIN_IDX
         , string_agg(val, '') as FULL_VAL
      from marked
     where 1=1
       and val != ''
     group by next_blank_line
     order by next_blank_line nulls last
)
, counted as
(
    select min_idx
         , full_val
         , (select count(distinct foo.match)
              from regexp_matches(full_val, '[a-z]', 'g') as foo(match)
           ) as UNIQUE_YES_ANSWERS
         --a different way, arguably more direct than using a regex function...
         , (select count(distinct foo.letter)
              from unnest(string_to_array(full_val, null)) as foo(letter)
           ) as UNIQUE_YES_ANSWERS_ALTERNATE
      from assembled 
     where 1=1
     order by min_idx
)
, part1_answer as
(
    select 'part 1'
         , sum(unique_yes_answers)
      from counted
     where 1=1
    --answer: 6809
)
, with_group_id as
(
    select idx as PERSON_ID
         , coalesce(next_blank_line, -1) as GROUP_ID
         , val
      from marked
     where 1=1
       and val != ''
     order by GROUP_ID
)
, exploded as
(
    select person_id
         , group_id
         , unnest(string_to_array(val, null)) as YES_TO_QUESTION
      from with_group_id 
     where 1=1
     order by group_id
)
, aggregated as
(
    select e.group_id
         , e.yes_to_question as QUESTION
         , g.group_size
         , count(1) as ANSWER_COUNT
      from exploded e
      join (select group_id
                 , count(1) as GROUP_SIZE
              from with_group_id
             where 1=1
             group by group_id
           ) g on(e.group_id = g.group_id)
     where 1=1
     group by e.group_id
            , QUESTION
            , g.group_size
)
, part2_answer as
(
    select 'part 2'
         , sum(answers_that_count)
      from (
            select group_id
                 , count(1) as ANSWERS_THAT_COUNT
              from aggregated
             where 1=1
               and answer_count = group_size
             group by group_id
           ) x
)
select * from part1_answer
union all
select * from part2_answer
;
