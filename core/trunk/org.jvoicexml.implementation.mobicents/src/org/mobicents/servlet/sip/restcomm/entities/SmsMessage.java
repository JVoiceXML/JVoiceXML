/*
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.mobicents.servlet.sip.restcomm.entities;

import java.math.BigDecimal;
import java.net.URI;

import org.joda.time.DateTime;

import org.mobicents.servlet.sip.restcomm.Sid;
import org.mobicents.servlet.sip.restcomm.annotations.concurrency.Immutable;
import org.mobicents.servlet.sip.restcomm.annotations.concurrency.NotThreadSafe;

/**
 * @author quintana.thomas@gmail.com (Thomas Quintana)
 */
@Immutable public final class SmsMessage {
  public static final int MAX_SIZE = 160;
  private final Sid sid;
  private final DateTime dateCreated;
  private final DateTime dateUpdated;
  private final DateTime dateSent;
  private final Sid accountSid;
  private final String sender;
  private final String recipient;
  private final String body;
  private final Status status;
  private final Direction direction;
  private final BigDecimal price;
  private final String apiVersion;
  private final URI uri;
  
  public SmsMessage(final Sid sid, final DateTime dateCreated, final DateTime dateUpdated, final DateTime dateSent,
      final Sid accountSid, final String sender, final String recipient, final String body, final Status status,
      final Direction direction, final BigDecimal price, final String apiVersion, final URI uri) {
    super();
    this.sid = sid;
    this.dateCreated = dateCreated;
    this.dateUpdated = dateUpdated;
    this.dateSent = dateSent;
    this.accountSid = accountSid;
    this.sender = sender;
    this.recipient = recipient;
    this.body = body;
    this.status = status;
    this.direction = direction;
    this.price = price;
    this.apiVersion = apiVersion;
    this.uri = uri;
  }
  
  public static Builder builder() {
    return new Builder();
  }

  public Sid getSid() {
    return sid;
  }

  public DateTime getDateCreated() {
    return dateCreated;
  }

  public DateTime getDateUpdated() {
    return dateUpdated;
  }

  public DateTime getDateSent() {
    return dateSent;
  }

  public Sid getAccountSid() {
    return accountSid;
  }

  public String getSender() {
    return sender;
  }

  public String getRecipient() {
    return recipient;
  }

  public String getBody() {
    return body;
  }

  public Status getStatus() {
    return status;
  }

  public Direction getDirection() {
    return direction;
  }

  public BigDecimal getPrice() {
    return price;
  }

  public String getApiVersion() {
    return apiVersion;
  }

  public URI getUri() {
    return uri;
  }
  
  public SmsMessage setDateSent(final DateTime dateSent) {
    return new SmsMessage(sid, dateCreated, DateTime.now(), dateSent, accountSid, sender, recipient,
        body, status, direction, price, apiVersion, uri);
  }
  
  public SmsMessage setStatus(final Status status) {
    return new SmsMessage(sid, dateCreated, DateTime.now(), dateSent, accountSid, sender, recipient,
        body, status, direction, price, apiVersion, uri);
  }
  
  @NotThreadSafe public static final class Builder {
    private Sid sid;
    private DateTime dateSent;
    private Sid accountSid;
    private String sender;
    private String recipient;
    private String body;
    private Status status;
    private Direction direction;
    private BigDecimal price;
    private String apiVersion;
    private URI uri;
	  
    private Builder() {
      super();
    }
    
    public SmsMessage build() {
      final DateTime now = DateTime.now();
      return new SmsMessage(sid, now, now, dateSent, accountSid, sender, recipient, body, status,
          direction, price, apiVersion, uri);
    }
    
    public void setSid(final Sid sid) {
      this.sid = sid;
    }
    
    public void setDateSent(final DateTime dateSent) {
      this.dateSent = dateSent;
    }
    
    public void setAccountSid(final Sid accountSid) {
      this.accountSid = accountSid;
    }
    
    public void setSender(final String sender) {
      this.sender = sender;
    }
    
    public void setRecipient(final String recipient) {
      this.recipient = recipient;
    }
    
    public void setBody(final String body) {
      this.body = body;
    }
    
    public void setStatus(final Status status) {
      this.status = status;
    }
    
    public void setDirection(final Direction direction) {
      this.direction = direction;
    }
    
    public void setPrice(final BigDecimal price) {
      this.price = price;
    }
    
    public void setApiVersion(final String apiVersion) {
      this.apiVersion = apiVersion;
    }
    
    public void setUri(final URI uri) {
      this.uri = uri;
    }
  }
  
  public enum Direction {
    INCOMING("incoming"),
    OUTBOUND_API("outbound-api");
    
    private final String text;
    private Direction(final String text) {
      this.text = text;
    }
    
    public static Direction getDirectionValue(final String text) {
      final Direction[] values = values();
      for(final Direction value : values) {
        if(value.toString().equals(text)) {
          return value;
        }
      }
      throw new IllegalArgumentException(text + " is not a valid direction.");
    }
    
    @Override public String toString() {
      return text;
    }
  }
  
  public enum Status {
    QUEUED("queued"),
    SENDING("sending"),
    SENT("sent"),
    FAILED("failed");
    
    private final String text;
    private Status(final String text) {
      this.text = text;
    }
    
    public static Status getStatusValue(final String text) {
      final Status[] values = values();
      for(final Status value : values) {
        if(values.toString().equals(text)) {
          return value;
        }
      }
      throw new IllegalArgumentException(text + " is not a valid status.");
    }
    
    @Override public String toString() {
      return text;
    }
  }
}
