package hongik.project.chatbot;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class ListViewAdapter extends BaseAdapter {

    private ArrayList<ListViewItem> itemList = new ArrayList<>() ;
    public ListViewAdapter() {}

    @Override
    public int getCount() {
        return itemList.size() ;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Context context = parent.getContext();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_item, parent, false);
        }

        TextView itemText = convertView.findViewById(R.id.item_text);
        TextView itemName = convertView.findViewById(R.id.item_name);
        LinearLayout listViewItemLayout = convertView.findViewById(R.id.listViewItemLayout);
        ListViewItem listViewItem = itemList.get(position);
        itemText.setText(listViewItem.getText());
        itemName.setText(listViewItem.getName());

        if (listViewItem.getName().equals("me")) {
            listViewItemLayout.setGravity(Gravity.RIGHT);
            itemText.setBackgroundColor(Color.rgb(255,255,0));
        } else {
            listViewItemLayout.setGravity(Gravity.LEFT);
            itemText.setBackgroundColor(Color.rgb(200,200,200));
        }

        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public ListViewItem getItem(int position) {
        return itemList.get(position) ;
    }

    public void addItem(String text, String name) {
        ListViewItem item = new ListViewItem(text, name);
        itemList.add(item);
    }

    public void clearList(){
        itemList.clear();
    }

    public void addList(ArrayList<ListViewItem> itemList){
        for(int i=0;i<itemList.size();i++)
            this.itemList.add(itemList.get(i));
    }
}
