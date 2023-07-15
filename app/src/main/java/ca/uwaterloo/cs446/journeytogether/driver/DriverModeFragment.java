package ca.uwaterloo.cs446.journeytogether.driver;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import ca.uwaterloo.cs446.journeytogether.R;

public class DriverModeFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_driver_mode, container, false);

        // 加载驾驶模式片段的布局文件
        // 进行界面元素的初始化和操作

        // 调用驾驶模式服务
        startDriverModeService();

        return view;
    }

    private void startDriverModeService() {
        Intent serviceIntent = new Intent(requireContext(), DriverModeService.class);
        ContextCompat.startForegroundService(requireContext(), serviceIntent);
    }

}

