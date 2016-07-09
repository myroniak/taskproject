package com.dadc.taskmanager.migration;

import io.realm.DynamicRealm;
import io.realm.FieldAttribute;
import io.realm.RealmMigration;
import io.realm.RealmSchema;

public class Migration implements RealmMigration {

    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {

        RealmSchema schema = realm.getSchema();

        if (oldVersion == 0) {
            schema.create("Task")
                    .addField("mId", String.class, FieldAttribute.PRIMARY_KEY)
                    .addField("mTitle", String.class)
                    .addField("mDescription", String.class)
                    .addField("mStartDateTask", Long.class)
                    .addField("mStopDateTask", Long.class)
                    .addField("maxTime", Long.class)
                    .addField("mTaskColor", Integer.class)
                    .addField("isSelected", Boolean.class);

            oldVersion++;
        }

        if (oldVersion == 1) {
            schema.get("Task")
                    .addField("mPauseStart", Long.class)
                    .addField("mPauseStop", Long.class)
                    .addField("mPauseDifferent", Long.class);
            oldVersion++;
        }

        if (oldVersion == 2) {
            schema.get("Task")
                    .addField("mUrl", String.class);

            schema.create("Statistic")
                    .addField("mId", String.class, FieldAttribute.PRIMARY_KEY)
                    .addField("mTitle", String.class)
                    .addField("mMonth", Integer.class)
                    .addField("mDifferentTime", Long.class);
            oldVersion++;
        }

    }
}