with patched as (select * from advent2020.input08)
select patched.line_num
     , patched.instruction
     , patched.argument
     , run_patched._c
  from patched
  join lateral (
                select count(1) as _C from patched
               ) run_patched on(true)
 where 1=1
 order by patched.line_num
;
