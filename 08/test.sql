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
    --virtual 0th line to get the program rolling
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
    select *
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
                 , (e.executed_lines || i.line_num::text || ',') as EXECUTED_LINES
                 , e.counter + 1 as COUNTER
              from execution e
              join advent2020.input08 i on(e.next_line_num = i.line_num)
             where 1=1
               and e.executed_lines not like ('%,' || i.line_num::text || ',%') --this is cleaner but makes it hard to show the terminal state
           ) x
     where 1=1
)
, part1_base as
(
    select counter
         , line_num
         , instruction
         , argument
         , accumulator
         , next_line_num
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
                       where 1=1
                     )
    --answer: 2034
)
select * from part1_base order by counter
;