/*
** WavpackContext.java
**
** Copyright (c) 2007 - 2013 Peter McQuillan
**
** All Rights Reserved.
**                       
** Distributed under the BSD Software License (see license.txt)  
**
*/

package com.beatofthedrum.wvdecoder;

public class WavpackContext
{
    WavpackConfig config = new WavpackConfig();
    WavpackStream stream = new WavpackStream();

   
    byte read_buffer[] = new byte[1024];	// was uchar in C
    public String error_message = "";
    public boolean error;

    java.io.DataInputStream infile;
    long total_samples, crc_errors, first_flags;		// was uint32_t in C
    int open_flags, norm_offset;
    int reduced_channels = 0;
    int lossy_blocks;
    int status = 0;	// 0 ok, 1 error
}