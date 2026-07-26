// Source code is decompiled from a .class file using FernFlower decompiler (from Intellij IDEA).
package org.rrgames.underground.sg;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class ZonedDateTimeAdapter implements JsonSerializer<ZonedDateTime>, JsonDeserializer<ZonedDateTime> {
   private static final DateTimeFormatter formatter;

   public ZonedDateTimeAdapter() {
   }

   public JsonElement serialize(ZonedDateTime src, Type typeOfSrc, JsonSerializationContext context) {
      return new JsonPrimitive(src.format(formatter));
   }

   public ZonedDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      return ZonedDateTime.parse(json.getAsString(), formatter);
   }

   static {
      formatter = DateTimeFormatter.ISO_ZONED_DATE_TIME;
   }
}
