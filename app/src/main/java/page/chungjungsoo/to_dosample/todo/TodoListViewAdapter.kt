package page.chungjungsoo.to_dosample.todo

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import page.chungjungsoo.to_dosample.R
import java.text.SimpleDateFormat
import java.util.*


class TodoListViewAdapter (context: Context, var resource: Int, var items: MutableList<Todo> ) : ArrayAdapter<Todo>(context, resource, items){
    private lateinit var db: TodoDatabaseHelper

    override fun getView(position: Int, convertView: View?, p2: ViewGroup): View {
        val layoutInflater : LayoutInflater = LayoutInflater.from(context)
        val view : View = layoutInflater.inflate(resource , null )
        val title : TextView = view.findViewById(R.id.listTitle)
        val description : TextView = view.findViewById(R.id.listDesciption)
        val date : TextView = view.findViewById(R.id.listDate)
        val finish : TextView = view.findViewById((R.id.finishText))
        val edit : Button = view.findViewById(R.id.editBtn)
        val delete : Button = view.findViewById(R.id.delBtn)

        db = TodoDatabaseHelper(this.context)

        // Get to-do item
        var todo = items[position]

        // Load title and description to single ListView item
        title.text = todo.title
        description.text = todo.description
        date.text = todo.date
        if(todo.finished){
            finish.text = "O"
            finish.setTextColor(Color.parseColor("#00ff00"))
        }
        else{
            finish.text = "X"
            finish.setTextColor(Color.parseColor("#ff0000"))
        }
        // OnClick Listener for edit button on every ListView items
        edit.setOnClickListener {
            // Very similar to the code in MainActivity.kt
            val builder = AlertDialog.Builder(this.context)
            val dialogView = layoutInflater.inflate(R.layout.add_todo_dialog, null)
            val titleToAdd = dialogView.findViewById<EditText>(R.id.todoTitle)
            val desciptionToAdd = dialogView.findViewById<EditText>(R.id.todoDescription)
            val dateToAdd = dialogView.findViewById<EditText>(R.id.todoDate)
            val timeToAdd = dialogView.findViewById<EditText>(R.id.todoTime)
            val finishToAdd = dialogView.findViewById<CheckBox>(R.id.finishCheck)
            val ime = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

            titleToAdd.setText(todo.title)
            desciptionToAdd.setText(todo.description)
            dateToAdd.setText(todo.date)
            timeToAdd.setText(todo.time)
            finishToAdd.isChecked = todo.finished
            titleToAdd.requestFocus()
            ime.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
            dateToAdd.setFocusable(false)
            timeToAdd.setFocusable(false)

            var cal = Calendar.getInstance()
            val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                val myFormat = "yyyy.MM.dd" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.KOREA)
                dateToAdd.setText(sdf.format(cal.time))

            }

            dateToAdd.setOnClickListener {
                DatePickerDialog(this.context, dateSetListener,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)).show()
            }


            val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)

                timeToAdd.setText(SimpleDateFormat("HH:mm").format(cal.time))
            }

            timeToAdd.setOnClickListener{
                TimePickerDialog(this.context, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(
                    Calendar.MINUTE), true).show()
            }

            builder.setView(dialogView)
                .setPositiveButton("수정") { _, _ ->
                    val tmp = Todo(
                        titleToAdd.text.toString(),
                        desciptionToAdd.text.toString(),
                        dateToAdd.text.toString(),
                        timeToAdd.text.toString(),
                        finishToAdd.isChecked
                    )

                    val result = db.updateTodo(tmp, position)

                    if (result) {
                        todo.title = titleToAdd.text.toString()
                        todo.description = desciptionToAdd.text.toString()
                        todo.date = dateToAdd.text.toString()
                        todo.time = timeToAdd.text.toString()
                        todo.finished = finishToAdd.isChecked()
                        notifyDataSetChanged()
                        ime.hideSoftInputFromWindow(titleToAdd.windowToken, 0)
                    }
                    else {
                        Toast.makeText(this.context, "수정 실패! :(", Toast.LENGTH_SHORT).show()
                        notifyDataSetChanged()
                    }
                }
                .setNegativeButton("취소") {_, _ ->
                    // Cancel Btn. Do nothing. Close keyboard.
                    ime.hideSoftInputFromWindow(titleToAdd.windowToken, 0)
                }
                .show()
        }

        // OnClick Listener for X(delete) button on every ListView items
        delete.setOnClickListener {
            val result = db.delTodo(position)
            if (result) {
                items.removeAt(position)
                notifyDataSetChanged()
            }
            else {
                Toast.makeText(this.context, "삭제 실패! :(", Toast.LENGTH_SHORT).show()
                notifyDataSetChanged()
            }
        }

        return view
    }
}