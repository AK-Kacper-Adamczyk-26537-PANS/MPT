package com.example.firebaseapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firebaseapp.R;
import com.example.firebaseapp.models.Task;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.TaskViewHolder> {

    private Context context;
    private List<Task> tasksList;
    private OnTaskClickListener listener;

    /**
     * Interfejs do obsługi kliknięć na zadania.
     */
    public interface OnTaskClickListener {
        void onTaskClick(Task task);
    }

    /**
     * Konstruktor adaptera.
     *
     * @param context   Kontekst aplikacji.
     * @param tasksList Lista zadań do wyświetlenia.
     * @param listener  Listener obsługujący kliknięcia.
     */
    public TasksAdapter(Context context, List<Task> tasksList, OnTaskClickListener listener) {
        this.context = context;
        this.tasksList = tasksList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TasksAdapter.TaskViewHolder holder, int position) {
        Task task = tasksList.get(position);
        holder.bind(task, listener);
    }

    @Override
    public int getItemCount() {
        return tasksList.size();
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTaskTitle, textViewTaskDescription, textViewTaskStartDate,
                textViewTaskEndDate, textViewTaskPriority, textViewTaskProgress, textViewTaskDueDate;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTaskTitle = itemView.findViewById(R.id.textViewTaskTitle);
            textViewTaskDescription = itemView.findViewById(R.id.textViewTaskDescription);
            textViewTaskStartDate = itemView.findViewById(R.id.textViewTaskStartDate);
            textViewTaskEndDate = itemView.findViewById(R.id.textViewTaskEndDate);
            textViewTaskPriority = itemView.findViewById(R.id.textViewTaskPriority);
            textViewTaskProgress = itemView.findViewById(R.id.textViewTaskProgress);
            textViewTaskDueDate = itemView.findViewById(R.id.textViewTaskDueDate);
        }

        /**
         * Binduje dane zadania do widoku i ustawia nasłuchiwanie kliknięć.
         *
         * @param task     Zadanie do wyświetlenia.
         * @param listener Listener obsługujący kliknięcia.
         */
        public void bind(final Task task, final OnTaskClickListener listener) {
            textViewTaskTitle.setText(task.getTitle());
            textViewTaskDescription.setText(task.getDescription());

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

            String startDateStr = task.getStartDate() != null ? sdf.format(task.getStartDate()) : "Brak";
            textViewTaskStartDate.setText("Data rozpoczęcia: " + startDateStr);

            String endDateStr = task.getEndDate() != null ? sdf.format(task.getEndDate()) : "Brak";
            textViewTaskEndDate.setText("Data zakończenia: " + endDateStr);

            textViewTaskPriority.setText("Priorytet: " + task.getPriority());

            // Przykład wyświetlania postępu jako gwiazdek
            StringBuilder progressStars = new StringBuilder("Postęp: ");
            for (int i = 0; i < task.getProgress(); i++) {
                progressStars.append("★");
            }
            for (int i = task.getProgress(); i < 5; i++) {
                progressStars.append("☆");
            }
            textViewTaskProgress.setText(progressStars.toString());

            textViewTaskDueDate.setText("Termin: " + sdf.format(task.getDueDate()));

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onTaskClick(task);
                }
            });
        }
    }
}
