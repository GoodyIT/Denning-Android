package it.denning.navigation.dashboard.section4.graph;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.denning.R;
import it.denning.model.GraphModel;

/**
 * Created by hothongmee on 08/11/2017.
 */

public class FeesAndGrowthAdapter extends RecyclerView.Adapter {
    private final List<GraphModel> graphModelList;

    public FeesAndGrowthAdapter(List<GraphModel> graphModelList) {
        this.graphModelList = graphModelList;
    }

    public class GraphViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.graph_title)
        TextView graphTitle;
        @BindView(R.id.chart)
        LineChart lineChart;

        public GraphViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_fee_growth, parent, false);
        return new GraphViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final GraphModel model = graphModelList.get(position);
        GraphViewHolder viewHolder = (GraphViewHolder)holder;
        viewHolder.graphTitle.setText(model.graphName);
        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < model.xValue.size(); i++) {
            entries.add(new Entry(i, model.yValue.get(i)));
        }
        LineDataSet lineDataSet = new LineDataSet(entries, model.yLegend);
        lineDataSet.setColor(Color.RED);
        LineData lineData = new LineData(lineDataSet);
        viewHolder.lineChart.setData(lineData);
        XAxis xAxis = viewHolder.lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(10f);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return model.xValue.get((int) value); // xVal is a string array
            }
        });
        viewHolder.lineChart.getAxisRight().setEnabled(false);

        viewHolder.lineChart.invalidate(); // refresh
    }

    @Override
    public int getItemCount() {
        return graphModelList.size();
    }

    public void swapItems(List<GraphModel> newSearchResultList) {

        this.graphModelList.clear();
        this.graphModelList.addAll(newSearchResultList);

        notifyDataSetChanged();
    }
}
