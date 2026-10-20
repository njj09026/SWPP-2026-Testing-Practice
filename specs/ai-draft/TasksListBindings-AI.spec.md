# Spec: `TasksListBindings.kt`

> Post-conditions are the test oracle: write the expected behavior from intent, not from the code.
> Tag each post-condition [Unit] if its test runs on the JVM; otherwise tag it [Integration].
> End each valid case with `Tested by <test>` if an existing test checks it, or `Tip:` describing the test to write.

## `setDueDateLabel(textView: TextView, task: Task?)`

**Intent**: Show the task's current due-date label in the supplied `TextView`. When there is no label, clear and hide the view, including when it previously displayed another task's label.

**Pre-conditions**
- `textView` is a valid `TextView`; `task` may be null. For date-dependent results, the device's local date is fixed as stated in each Tip.

**Post-conditions**
- Success cases
  - A completed task displays `CLEAR` and makes the view visible, even without a due date. `[Integration]`
    - Tip: Given a `TextView` with `text = ""` and `visibility = GONE`, and a `Task` with `dueDate = null` and `isCompleted = true` / When `setDueDateLabel` is called / Then `text = "CLEAR"` and `visibility = VISIBLE`.
  - An active task with a due date displays the matching calendar-day label and makes the view visible. `[Integration]`
    - Tip: Given device local date `2026-10-20`, a `TextView` with `text = ""` and `visibility = GONE`, and a `Task` with `dueDate = 2026-10-25` and `isCompleted = false` / When `setDueDateLabel` is called / Then `text = "D-5"` and `visibility = VISIBLE`.
  - An active task without a due date has no visible label. `[Integration]`
    - Tip: Given a `TextView` with `text = ""` and `visibility = VISIBLE`, and a `Task` with `dueDate = null` and `isCompleted = false` / When `setDueDateLabel` is called / Then `text = ""` and `visibility = GONE`.
  - A null task has no visible label. `[Integration]`
    - Tip: Given a `TextView` with `text = ""` and `visibility = VISIBLE`, and `task = null` / When `setDueDateLabel` is called / Then `text = ""` and `visibility = GONE`.
- Error cases
  - None.
- Edge cases
  - A view reused for an active task without a due date loses the previous task's label. `[Integration]`
    - Tip: Given a `TextView` with `text = "D-5"` and `visibility = VISIBLE`, and a `Task` with `dueDate = null` and `isCompleted = false` / When `setDueDateLabel` is called / Then `text = ""` and `visibility = GONE`.
  - A view reused for a null task loses the previous task's label. `[Integration]`
    - Tip: Given a `TextView` with `text = "CLEAR"` and `visibility = VISIBLE`, and `task = null` / When `setDueDateLabel` is called / Then `text = ""` and `visibility = GONE`.
  - At one day before the device's local date, an active task displays `D+1`. `[Integration]`
    - Tip: Given device local date `2026-10-20`, a `TextView` with `text = ""` and `visibility = GONE`, and a `Task` with `dueDate = 2026-10-19` and `isCompleted = false` / When `setDueDateLabel` is called / Then `text = "D+1"` and `visibility = VISIBLE`.
  - At the device's local date, an active task displays `D-Day`. `[Integration]`
    - Tip: Given device local date `2026-10-20`, a `TextView` with `text = ""` and `visibility = GONE`, and a `Task` with `dueDate = 2026-10-20` and `isCompleted = false` / When `setDueDateLabel` is called / Then `text = "D-Day"` and `visibility = VISIBLE`.
  - At one day after the device's local date, an active task displays `D-1`. `[Integration]`
    - Tip: Given device local date `2026-10-20`, a `TextView` with `text = ""` and `visibility = GONE`, and a `Task` with `dueDate = 2026-10-21` and `isCompleted = false` / When `setDueDateLabel` is called / Then `text = "D-1"` and `visibility = VISIBLE`.

**Undefined behavior** (optional)
- None.
