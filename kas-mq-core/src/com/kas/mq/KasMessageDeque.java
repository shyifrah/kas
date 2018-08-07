package com.kas.mq;

import java.util.concurrent.LinkedBlockingDeque;

/**
 * {@link KasMessageDeque} is the actual container for {@link KasMessage}
 */
public class KasMessageDeque extends LinkedBlockingDeque<KasMessage>
{
  private static final long serialVersionUID = 1L;
}
