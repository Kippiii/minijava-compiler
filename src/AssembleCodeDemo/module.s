.set   SYS_exit, 1
.set   SP_TRAP_LINUX, 0x90

.macro exit_program
clr     %o0             ! %o0 := 0;  program status=0=success
mov     SYS_exit, %g1   ! %g1 := SYS_exit; determine system call
ta      SP_TRAP_LINUX
.endm
