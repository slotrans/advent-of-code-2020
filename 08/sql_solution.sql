/*
postgres=# create table advent2020.input08 ( line_num serial primary key, instruction text not null, argument int not null ) ;
CREATE TABLE
postgres=# \copy advent2020.input08(instruction, argument) from 'input08' with ( delimiter ' ' ) ;
COPY 631
postgres=# select * from advent2020.input08 order by line_num limit 20 ;
 line_num | instruction | argument
----------+-------------+----------
        1 | nop         |      355
        2 | acc         |       46
        3 | jmp         |       42
        4 | jmp         |      585
        5 | acc         |       11
        6 | acc         |        0
        7 | acc         |       40
        8 | jmp         |      284
        9 | acc         |       -2
       10 | nop         |      276
       11 | jmp         |      613
       12 | acc         |       23
       13 | acc         |        2
       14 | acc         |       14
       15 | acc         |       25
       16 | jmp         |      310
       17 | acc         |       43
       18 | acc         |       43
       19 | jmp         |      510
       20 | jmp         |      116
(20 rows)
*/

with recursive execution( line_num
                        , instruction
                        , argument
                        , accumulator
                        , next_line_num
                        , executed_lines
                        , counter
                        , program_state
                        )
as
(
    --virtual 0th line to get the program rolling
    select 0 as LINE_NUM
         , 'nop' as INSTRUCTION
         , 0 as ARGUMENT
         , 0 as ACCUMULATOR
         , 1 as NEXT_LINE_NUM
         , ',' as EXECUTED_LINES
         , 0 as COUNTER
         , 'normal' as PROGRAM_STATE
    --
    union all
    --
    select *
      from (
            select x.line_num
                 , x.instruction
                 , x.argument
                 , x.accumulator
                 , x.next_line_num
                 , (x.executed_lines || x.line_num::text || ',') as EXECUTED_LINES --this append cannot be done in the inner query or it will falsify the program_state check
                 , x.counter
                 , case when x.executed_lines like ('%,' || x.line_num::text || ',%')
                        then 'looped'
                        when x.executed_lines like ('%,' || x.next_line_num::text || ',%')
                        then 'about to loop'
                        else 'normal'
                         end as PROGRAM_STATE --this lets us report on how we stopped                
              from (
                    select i.line_num
                         , i.instruction
                         , i.argument
                         , e.accumulator + (case i.instruction
                                            when 'acc' then i.argument
                                            when 'jmp' then 0
                                            when 'nop' then 0
                                             end) as ACCUMULATOR
                         , i.line_num + (case i.instruction
                                         when 'acc' then 1
                                         when 'jmp' then i.argument
                                         when 'nop' then 1
                                          end) as NEXT_LINE_NUM
                         , e.executed_lines
                         , e.counter + 1 as COUNTER
                      from execution e
                      join advent2020.input08 i on(e.next_line_num = i.line_num)
                     where 1=1
                       --and e.executed_lines not like ('%,' || i.line_num::text || ',%') --this is cleaner but makes it hard to show the terminal state
                   ) x
             where 1=1
           ) z
     where 1=1
       and z.program_state in ( 'normal', 'about to loop' )
)
, part1_base as
(
    select counter
         , line_num
         , instruction
         , argument
         , accumulator
         , program_state
         , next_line_num
         , executed_lines
      from execution
     where 1=1
     order by counter
)
, part1_answer as
(
    select 'part 1'
         , accumulator
      from part1_base
     where 1=1
       and counter = (select max(counter) 
                        from part1_base 
                       where program_state in ( 'normal', 'about to loop' )
                     )
    --answer: 2034
)
select * from part1_answer
;


--part 2
with var as
(
    select max(line_num) as MAX_LINE_NUM
      from advent2020.input08
     where 1=1
)
, lines_to_patch as
(
    select line_num
         , instruction
      from advent2020.input08
     where 1=1
       and instruction in ( 'jmp', 'nop' )
)
select ltp.line_num as PATCHED_LINE_NUM
     , ltp.instruction as ORIG_INSTRUCTION
     , run_patched.program_state
     , run_patched.accumulator
  from lines_to_patch ltp
  join lateral (
                with recursive execution( line_num
                                        , instruction
                                        , argument
                                        , accumulator
                                        , next_line_num
                                        , executed_lines
                                        , counter
                                        , program_state
                                        )
                as
                (
                    --virtual 0th line to get the program rolling
                    select 0 as LINE_NUM
                         , 'nop' as INSTRUCTION
                         , 0 as ARGUMENT
                         , 0 as ACCUMULATOR
                         , 1 as NEXT_LINE_NUM
                         , ',' as EXECUTED_LINES
                         , 0 as COUNTER
                         , 'normal' as PROGRAM_STATE
                    --
                    union all
                    --
                    select *
                      from (
                            select x.line_num
                                 , x.instruction
                                 , x.argument
                                 , x.accumulator
                                 , x.next_line_num
                                 , (x.executed_lines || x.line_num::text || ',') as EXECUTED_LINES --this append cannot be done in the inner query or it will falsify the program_state check
                                 , x.counter
                                 , case when x.executed_lines like ('%,' || x.line_num::text || ',%')
                                        then 'looped'
                                        when x.executed_lines like ('%,' || x.next_line_num::text || ',%')
                                        then 'about to loop'
                                        when x.next_line_num > (select max_line_num from var)
                                        then 'about to terminate'
                                        else 'normal'
                                         end as PROGRAM_STATE --this lets us report on how we stopped                
                              from (
                                    select i.line_num
                                         , i.instruction
                                         , i.argument
                                         , e.accumulator + (case i.instruction
                                                            when 'acc' then i.argument
                                                            when 'jmp' then 0
                                                            when 'nop' then 0
                                                             end) as ACCUMULATOR
                                         , i.line_num + (case i.instruction
                                                         when 'acc' then 1
                                                         when 'jmp' then i.argument
                                                         when 'nop' then 1
                                                          end) as NEXT_LINE_NUM
                                         , e.executed_lines
                                         , e.counter + 1 as COUNTER
                                      from execution e
                                      join (
                                            --emits patched code
                                            select c.line_num
                                                 , case when c.line_num = ltp.line_num
                                                        then (case c.instruction
                                                              when 'jmp' then 'nop'
                                                              when 'nop' then 'jmp'
                                                               end) 
                                                        else c.instruction
                                                         end as INSTRUCTION
                                                 , c.argument
                                              from advent2020.input08 c
                                             where 1=1
                                           ) i on(e.next_line_num = i.line_num)
                                     where 1=1
                                   ) x
                             where 1=1
                           ) z
                     where 1=1
                       and z.program_state in ( 'normal', 'about to loop', 'about to terminate' )
                )
                select * from execution
               ) run_patched on(run_patched.program_state like 'about%')
 where 1=1
 order by PATCHED_LINE_NUM
;
--patching line 329 (1-based) leads to termination with an accumulator value of 672
