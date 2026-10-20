# Spec: `TasksListBindings.kt`

> Post-conditions are the test oracle: write the expected behavior from intent, not from the code.
> Tag each post-condition [Unit] if its test runs on the JVM; otherwise tag it [Integration].
> End each valid case with `Tested by <test>` if an existing test checks it, or `Tip:` describing the test to write.

## `setDueDateLabel(textView: TextView, task: Task?)`

**Intent**: Binding adapter for the D-day label of one row in the task list (`app:dueDateLabel="@{task}"` on `due_date_text`). It shows the label of `task` for the device's current local date (`CLEAR`, `D-Day`, `D-N`, `D+N`, as in `DueDateUtils.spec.md`), and shows no label for an active task without a due date or a null task. The label must follow the task's current state every time the row is bound again, because checking or unchecking a task rebinds the same row view.

**Pre-conditions**
- Called on the main thread (by data binding; in tests, inside `InstrumentationRegistry.getInstrumentation().runOnMainSync { … }`).
- `task` may be null (the row is bound before its data arrives).
- `textView` may already show any text with any visibility from an earlier binding (a recycled or rebound row).
- "today" is the device's `LocalDate.now()` at the time of the call. Production code reads the system clock, so tests cannot fix the date: every Tip gives `dueDate` relative to `today = LocalDate.now()` and the expected label as an exact string.
- "Shows label X" means `textView.text == X` and `textView.visibility == View.VISIBLE`. "Shows no label" means `textView.text == ""` or `textView.visibility != View.VISIBLE` (either way no label text can be seen).

**Post-conditions**
- Success cases
  - A completed task without a due date shows `CLEAR`. `[Integration]`
    - Tip: Given a new `TextView` and `Task("T", "D", isCompleted = true, dueDate = null)` / When `setDueDateLabel` is called / Then text is `"CLEAR"` and visibility is `VISIBLE`.
  - A completed task with a future due date shows `CLEAR`, not `D-N`. `[Integration]`
    - Tip: Given a new `TextView` and `Task("T", "D", isCompleted = true, dueDate = today + 5 days)` / When `setDueDateLabel` is called / Then text is `"CLEAR"` and visibility is `VISIBLE`.
  - An active task due in 5 days shows `D-5`. `[Integration]`
    - Tip: Given a new `TextView` and `Task("T", "D", isCompleted = false, dueDate = today + 5 days)` / When `setDueDateLabel` is called / Then text is `"D-5"` and visibility is `VISIBLE`.
  - An active task overdue by 5 days shows `D+5`. `[Integration]`
    - Tip: Given a new `TextView` and `Task("T", "D", isCompleted = false, dueDate = today - 5 days)` / When `setDueDateLabel` is called / Then text is `"D+5"` and visibility is `VISIBLE`.
  - An active task due today shows `D-Day`. `[Integration]`
    - Tip: Given a new `TextView` and `Task("T", "D", isCompleted = false, dueDate = today)` / When `setDueDateLabel` is called / Then text is `"D-Day"` and visibility is `VISIBLE`.
  - An active task without a due date shows no label. `[Integration]`
    - Tip: Given a new `TextView` and `Task("T", "D", isCompleted = false, dueDate = null)` / When `setDueDateLabel` is called / Then text is `""` or visibility is not `VISIBLE`.
  - A null task shows no label. `[Integration]`
    - Tip: Given a new `TextView` and `task = null` / When `setDueDateLabel` is called / Then text is `""` or visibility is not `VISIBLE`.
- Error cases
  - None. Every `Task?` value has a defined label.
- Edge cases
  - D-Day boundary, one day overdue: shows `D+1`. `[Integration]`
    - Tip: Given a new `TextView` and an active task with `dueDate = today - 1 day` / When `setDueDateLabel` is called / Then text is `"D+1"` and visibility is `VISIBLE`.
  - D-Day boundary, due in one day: shows `D-1`. `[Integration]`
    - Tip: Given a new `TextView` and an active task with `dueDate = today + 1 day` / When `setDueDateLabel` is called / Then text is `"D-1"` and visibility is `VISIBLE`.
  - Beyond one calendar month in the future, the row shows the total day count. `today + 31 days` is always at least one calendar month away, so this checks the count through the binding. `[Integration]`
    - Tip: Given a new `TextView` and an active task with `dueDate = today + 31 days` / When `setDueDateLabel` is called / Then text is `"D-31"` and visibility is `VISIBLE`.
  - Beyond one calendar month overdue, the row shows the total day count. `[Integration]`
    - Tip: Given a new `TextView` and an active task with `dueDate = today - 31 days` / When `setDueDateLabel` is called / Then text is `"D+31"` and visibility is `VISIBLE`.
  - Unchecking a completed task without a due date removes `CLEAR` (completed → active boundary, no due date). `[Integration]`
    - Tip: Given a `TextView` already bound to `Task("T", "D", isCompleted = true, dueDate = null, id = "id1")` (shows `"CLEAR"`) / When `setDueDateLabel` is called again with the same task but `isCompleted = false` / Then text is `""` or visibility is not `VISIBLE`.
  - Unchecking a completed task with a due date replaces `CLEAR` with the D-day label (completed → active boundary, with due date). `[Integration]`
    - Tip: Given a `TextView` already bound to a completed task with `dueDate = today + 3 days` (shows `"CLEAR"`) / When `setDueDateLabel` is called again with the same task but `isCompleted = false` / Then text is `"D-3"` and visibility is `VISIBLE`.
  - Checking an active task with a due date replaces the D-day label with `CLEAR` (active → completed boundary). `[Integration]`
    - Tip: Given a `TextView` already bound to an active task with `dueDate = today + 3 days` (shows `"D-3"`) / When `setDueDateLabel` is called again with the same task but `isCompleted = true` / Then text is `"CLEAR"` and visibility is `VISIBLE`.
  - A recycled row bound to an active task without a due date drops the previous row's label. `[Integration]`
    - Tip: Given a `TextView` already bound to an active task with `dueDate = today + 5 days` (shows `"D-5"`) / When `setDueDateLabel` is called with another active task with `dueDate = null` / Then text is `""` or visibility is not `VISIBLE`.
  - A row rebound to a null task drops the previous label. `[Integration]`
    - Tip: Given a `TextView` already bound to a completed task (shows `"CLEAR"`) / When `setDueDateLabel` is called with `task = null` / Then text is `""` or visibility is not `VISIBLE`.
  - In the task list screen, checking and then unchecking a task without a due date shows `CLEAR` and then no label. `[Integration]`
    - Tip: Given the fake repository holds only `Task("TITLE1", "DESCRIPTION1", isCompleted = false, id = "id1", dueDate = null)` and `TasksFragment` is launched / When the user taps the row's `complete_checkbox` once / Then the row's `due_date_text` shows `"CLEAR"`; When the user taps it again / Then the row's `due_date_text` shows no label.
  - In the task list screen, checking and then unchecking a task due in 3 days shows `D-3`, `CLEAR`, then `D-3` again. `[Integration]`
    - Tip: Given the fake repository holds only `Task("TITLE1", "DESCRIPTION1", isCompleted = false, id = "id1", dueDate = today + 3 days)` and `TasksFragment` is launched / Then the row's `due_date_text` shows `"D-3"`; When the user taps `complete_checkbox` / Then it shows `"CLEAR"`; When the user taps it again / Then it shows `"D-3"`.

**Undefined behavior** (optional)
- If the local date changes (at midnight) while a row is on screen, the label is not refreshed until the row is bound again; refreshing on a date change is outside this function.
- Whether "no label" is done with empty text, `INVISIBLE` or `GONE` is left open; the intent only requires that no label text can be seen.
