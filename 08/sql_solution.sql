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
    select i.line_num
         , i.instruction
         , i.argument
         , e.accumulator + (case i.instruction
                            when 'acc' then i.argument
                            when 'jmp' then 0
                            when 'nop' then 0
                             end
                           ) as ACCUMULATOR
         , i.line_num + (case i.instruction
                         when 'acc' then 1
                         when 'jmp' then i.argument
                         when 'nop' then 1
                          end
                        ) as NEXT_LINE_NUM
         , (e.executed_lines || i.line_num::text || ',') as EXECUTED_LINES
         , e.counter + 1 as COUNTER
         , case when e.executed_lines like ('%,' || i.line_num::text || ',%')
                then 'looped'
                else 'normal'
                 end as PROGRAM_STATE --this lets us get one more row after finding the loop -- or termination in p2 -- so we can report on how we stopped
      from execution e
      join advent2020.input08 i on(e.next_line_num = i.line_num)
     where 1=1
       --and e.executed_lines not like ('%,' || i.line_num::text || ',%') --this is cleaner but makes it hard to show the terminal state
       and e.program_state = 'normal'
)
, part1_base as
(
    select counter
         , line_num
         , instruction
         , argument
         , accumulator
         , program_state
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
       and counter = (select max(counter) from part1_base where program_state = 'normal')
    --answer: 2034
)
select * from part1_answer
;

/*
select p.line_num
     , p.instruction
     , p.argument
     , 
  from (
        select p.line_num
             , (case p.instruction
                when 'jmp' then 'nop'
                when 'nop' then 'jmp'
                 end) as INSTRUCTION
             , p.argument
          from advent2020.input08
         where 1=1
           and p.instruction in ( 'jmp', 'nop' )
       ) patched
  join lateral (
                with recursive execution( line_num
                                        , instruction
                                        , argument
                                        , accumulator
                                        , next_line_num
                                        , executed_lines
                                        , counter
                                        )
                as
                (
                    select 0 as LINE_NUM
                         , 'nop' as INSTRUCTION
                         , 0 as ARGUMENT
                         , 0 as ACCUMULATOR
                         , 1 as NEXT_LINE_NUM
                         , ',' as EXECUTED_LINES
                         , 0 as COUNTER
                    --
                    union all
                    --
                    select i.line_num
                         , i.instruction
                         , i.argument
                         , e.accumulator + (case i.instruction
                                            when 'acc' then i.argument
                                            when 'jmp' then 0
                                            when 'nop' then 0
                                             end
                                           ) as ACCUMULATOR
                         , i.line_num + (case i.instruction
                                         when 'acc' then 1
                                         when 'jmp' then i.argument
                                         when 'nop' then 1
                                          end
                                        ) as NEXT_LINE_NUM
                         , (e.executed_lines || i.line_num::text || ',') as EXECUTED_LINES
                         , e.counter + 1 as COUNTER
                      from execution e
                      join advent2020.input08 i on(e.next_line_num = i.line_num)
                     where 1=1
                       and e.executed_lines not like ('%,' || i.line_num::text || ',%')     
                )
                    
               ) foo on(true)
 where 1=1
   and i.instruction in ( 'jmp', 'nop' )
 order by i.line_num
;
*/