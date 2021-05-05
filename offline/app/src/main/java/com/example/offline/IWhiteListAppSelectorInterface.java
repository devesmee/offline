package com.example.offline;

import java.util.ArrayList;

public interface IWhiteListAppSelectorInterface {
    void onAppSelected(ArrayList<String> systemApps);

    void onAppDeselected(ArrayList<String> systemApps);
}
