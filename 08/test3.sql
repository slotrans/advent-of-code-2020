with lines_to_patch as
(
    select line_num
         , instruction
      from advent2020.input08
     where 1=1
       and instruction in ( 'jmp', 'nop' )
)
select ltp.line_num as PATCHED_LINE_NUM
     , ltp.instruction as ORIG_INSTRUCTION
     , patched_code.line_num
     , patched_code.instruction
     , patched_code.argument
  from lines_to_patch ltp
  join lateral (select i.line_num
                     , case when i.line_num = ltp.line_num
                            then (case i.instruction
                                  when 'jmp' then 'nop'
                                  when 'nop' then 'jmp'
                                   end) 
                            else i.instruction
                             end as INSTRUCTION
                     , i.argument
                  from advent2020.input08 i
                 where 1=1
               ) patched_code on(true)
 where 1=1
 order by PATCHED_LINE_NUM
        , patched_code.line_num
;
