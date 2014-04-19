MotoX_LED
=========

Control for the charging LED in the Moto X and other new Motorola phones with Active Display

It was discovered that the Moto X (and by extension others like the Droid Ultra and Moto G) has a charging light that kicks in when the device is fully dead and can't turn on the screen.  Shell commands were discovered that could trigger this LED while the phone is operating normally. This app controls that LED.

Of course props to whomever figured out these shell commands and to the dev of RootTools.

Once the requirement of the phone being plugged in for the LED to turn on is bypassed, this app could become a whole lot more. But that's a big road block.

In the meantime this will simply turn on your LED based on the setting chosen and based on whether your battery is fully charged or not. This app was designed for the Moto X only (4.4 rooted) but has been confirmed to work on the new DROIDs too (Maxx & Ultra)

Usage:

    Select an option for the LED: heartbeat (pulses like a heartbeat), battery-charging (solid while charging), battery-full (solid when full), battery-charging-or-full (solid when plugged in), mmc0 (pulses based on disk reads/writes)
    Select a brightness.
    Save if you want to use your choice for when the device is charging or it's battery full - or hit both buttons to save it for both scenarios.
    Hit Execute to save preferences

More Info: http://forum.xda-developers.com/showthread.php?t=2602689

