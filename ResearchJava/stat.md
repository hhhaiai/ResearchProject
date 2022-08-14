# stat命令

``` shell
$adb shell stat --help
usage: stat [-tfL] [-c FORMAT] FILE...

Display status of files or filesystems.

-c      Output specified FORMAT string instead of default
-f      Display filesystem status instead of file status
-L      Follow symlinks
-t      terse (-c "%n %s %b %f %u %g %D %i %h %t %T %X %Y %Z %o")
              (with -f = -c "%n %i %l %t %s %S %b %f %a %c %d")

The valid format escape sequences for files:
%a  Access bits (octal) |%A  Access bits (flags)|%b  Size/512
%B  Bytes per %b (512)  |%d  Device ID (dec)    |%D  Device ID (hex)
%f  All mode bits (hex) |%F  File type          |%g  Group ID
%G  Group name          |%h  Hard links         |%i  Inode
%m  Mount point         |%n  Filename           |%N  Long filename
%o  I/O block size      |%s  Size (bytes)       |%t  Devtype major (hex)
%T  Devtype minor (hex) |%u  User ID            |%U  User name
%x  Access time         |%X  Access unix time   |%y  Modification time
%Y  Mod unix time       |%z  Creation time      |%Z  Creation unix time

The valid format escape sequences for filesystems:
%a  Available blocks    |%b  Total blocks       |%c  Total inodes
%d  Free inodes         |%f  Free blocks        |%i  File system ID
%l  Max filename length |%n  File name          |%s  Fragment size
%S  Best transfer size  |%t  FS type (hex)      |%T  FS type (driver name)
```