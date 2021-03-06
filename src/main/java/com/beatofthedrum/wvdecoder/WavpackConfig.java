/*
** WavpackConfig.java
**
** Copyright (c) 2007 - 2013 Peter McQuillan
**
** All Rights Reserved.
**                       
** Distributed under the BSD Software License (see license.txt)  
**
*/

package com.beatofthedrum.wvdecoder;

class WavpackConfig
{
    int bits_per_sample, bytes_per_sample;
    int num_channels, float_norm_exp;
    long flags, sample_rate, channel_mask;	// was uint32_t in C
}