package com.myapp.pizzahut_admin;

import android.graphics.Color;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class SalesActivity extends AppCompatActivity {

    private BarChart barChart;
    private HorizontalBarChart horizontalBarChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_sales);
        setContentView(R.layout.activity_sales);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });



        barChart = findViewById(R.id.barChart);
        horizontalBarChart = findViewById(R.id.horizontalBarChart);

        setupBarChart();
        setupHorizontalBarChart();

        loadWeeklySales();
        loadBranchSales();

        findViewById(R.id.weeklySalesButton).setOnClickListener(v -> loadWeeklySales());
        findViewById(R.id.monthlySalesButton).setOnClickListener(v -> loadMonthlySales());
    }

    private void setupBarChart() {
        barChart.getDescription().setEnabled(false);
        barChart.setDrawGridBackground(false);
        barChart.setDrawBarShadow(false);
        barChart.setPinchZoom(false);
        barChart.setDrawValueAboveBar(true);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(7);

        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setGranularity(1f);

        YAxis rightAxis = barChart.getAxisRight();
        rightAxis.setEnabled(false);
    }

    private void setupHorizontalBarChart() {
        horizontalBarChart.getDescription().setEnabled(false);
        horizontalBarChart.setDrawGridBackground(false);
        horizontalBarChart.setDrawBarShadow(false);
        horizontalBarChart.setPinchZoom(false);
        horizontalBarChart.setDrawValueAboveBar(true);

        XAxis xAxis = horizontalBarChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);

        YAxis leftAxis = horizontalBarChart.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setGranularity(1f);

        YAxis rightAxis = horizontalBarChart.getAxisRight();
        rightAxis.setEnabled(false);
    }

    private void loadWeeklySales() {
        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, 120000));
        entries.add(new BarEntry(1, 150000));
        entries.add(new BarEntry(2, 180000));
        entries.add(new BarEntry(3, 200000));
        entries.add(new BarEntry(4, 220000));
        entries.add(new BarEntry(5, 250000));
        entries.add(new BarEntry(6, 300000));

        BarDataSet dataSet = new BarDataSet(entries, "Weekly Sales");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(12f);

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.9f);

        String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(days));

        barChart.setData(barData);
        barChart.animateY(1000);
        barChart.invalidate();
    }

    private void loadMonthlySales() {
        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, 500000));
        entries.add(new BarEntry(1, 700000));
        entries.add(new BarEntry(2, 900000));
        entries.add(new BarEntry(3, 1200000));

        BarDataSet dataSet = new BarDataSet(entries, "Monthly Sales");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(12f);

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.9f);

        String[] weeks = {"Week 1", "Week 2", "Week 3", "Week 4"};
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(weeks));

        barChart.setData(barData);
        barChart.animateY(1000);
        barChart.invalidate();
    }

    private void loadBranchSales() {
        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, 1200000));
        entries.add(new BarEntry(1, 900000));
        entries.add(new BarEntry(2, 750000));
        entries.add(new BarEntry(3, 600000));
        entries.add(new BarEntry(4, 800000));
        entries.add(new BarEntry(5, 300000));
        entries.add(new BarEntry(6, 500000));

        BarDataSet dataSet = new BarDataSet(entries, "Branch Sales");
        dataSet.setColors(ColorTemplate.PASTEL_COLORS);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(12f);

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.9f);

        String[] branches = {"Colombo", "Kandy", "Galle", "Jaffna","Rathnapura","Matara","Kurunegala"};
        YAxis yAxis = horizontalBarChart.getAxisLeft();
        yAxis.setValueFormatter(new IndexAxisValueFormatter(branches));

        horizontalBarChart.setData(barData);
        horizontalBarChart.animateY(1000);
        horizontalBarChart.invalidate();
    }
}