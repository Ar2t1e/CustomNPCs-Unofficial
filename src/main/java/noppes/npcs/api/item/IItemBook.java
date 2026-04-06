package noppes.npcs.api.item;

import noppes.npcs.api.interfaces.ParamName;

public interface IItemBook extends IItemStack {

   String[] getText();

   void setText(@ParamName("pages") String ... pages);

   String getAuthor();

   void setAuthor(@ParamName("author") String author);

   String getTitle();

   void setTitle(@ParamName("title") String title);

}
