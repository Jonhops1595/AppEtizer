package com.example.appetizer;

public class DropDownCategories {

        private int id;
        private String title;
        private boolean selected;

        public DropDownCategories(int id, String title){
            this.id = id;
            this.title = title;
            setSelected(false);
        }

        public String getTitle() {
            return title;
        }

        public int getId() { return id; }

        public void setTitle(String title) {
            this.title = title;
        }

        public boolean isSelected() {
            return selected;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }
}
