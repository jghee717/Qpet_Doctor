package com.ccit19.merdog_doctor;

import java.util.Collection;

public class RecordsViewHolder {
        public String user_name;
        public String pet_name;
        public String date;
        public String chat_id;


        public RecordsViewHolder(String user_name, String pet_name, String date, String chat_id) {
                this.user_name = user_name;
                this.pet_name = pet_name;
                this.date = date;
                this.date = chat_id;
        }
        public String get_user_name() {
                return user_name;
        }

        public String getdate() {
                return date;
        }

        public String get_pet_name() {
                return pet_name;
        }

}