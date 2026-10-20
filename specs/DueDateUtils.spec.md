# Spec: `DueDateUtils.kt`

> Post-conditions are the test oracle: write the expected behavior from intent, not from the code.
> Tag each post-condition [Unit] if its test runs on the JVM; otherwise tag it [Integration].
> End each valid case with `Tested by <test>` if an existing test checks it, or `Tip:` describing the test to write.

## `dueDateLabel(dueDate: LocalDate?, today: LocalDate, isCompleted: Boolean): String`

**Intent**: Return the D-day label shown next to a task in the task list. A completed task is always `CLEAR`. An active task shows nothing without a due date, `D-Day` when it is due `today`, `D-N` when it is due in N calendar days, and `D+N` when it is overdue by N calendar days. N is the total number of calendar days between `today` and `dueDate` (for example, one month later can be 28 to 31 days), not the day-of-month part of the difference.

**Pre-conditions**
- `today` is the calendar date to compare against (the caller passes the device's local date).
- `dueDate` may be null (the task has no due date).
- The label format is exact: `CLEAR`, `D-Day`, `D-<N>` and `D+<N>` with N as a plain decimal number (no padding, spaces or separators), or the empty string.

**Post-conditions**
- Success cases
  - A completed task with a future due date returns `CLEAR`. `[Unit]`
    - Tip: Given `dueDate = 2026-10-25`, `today = 2026-10-20`, `isCompleted = true` / When `dueDateLabel` is called / Then it returns `"CLEAR"`.
  - A completed task without a due date returns `CLEAR`. `[Unit]`
    - Tip: Given `dueDate = null`, `today = 2026-10-20`, `isCompleted = true` / When `dueDateLabel` is called / Then it returns `"CLEAR"`.
  - A completed task whose due date has passed returns `CLEAR`, not `D+N` (completion takes precedence). `[Unit]`
    - Tip: Given `dueDate = 2026-10-15`, `today = 2026-10-20`, `isCompleted = true` / When `dueDateLabel` is called / Then it returns `"CLEAR"`.
  - An active task without a due date returns the empty string (no label). `[Unit]`
    - Tip: Given `dueDate = null`, `today = 2026-10-20`, `isCompleted = false` / When `dueDateLabel` is called / Then it returns `""`.
  - An active task due today returns `D-Day`. `[Unit]`
    - Tip: Given `dueDate = 2026-10-20`, `today = 2026-10-20`, `isCompleted = false` / When `dueDateLabel` is called / Then it returns `"D-Day"`.
  - An active task due in N calendar days returns `D-N`. `[Unit]`
    - Tip: Given `dueDate = 2026-10-25`, `today = 2026-10-20`, `isCompleted = false` / When `dueDateLabel` is called / Then it returns `"D-5"`.
  - An active task overdue by N calendar days returns `D+N`. `[Unit]`
    - Tip: Given `dueDate = 2026-10-15`, `today = 2026-10-20`, `isCompleted = false` / When `dueDateLabel` is called / Then it returns `"D+5"`.
- Error cases
  - None. Every combination of a nullable `dueDate`, a `today` and `isCompleted` has a defined label.
- Edge cases
  - D-Day boundary, one day overdue: returns `D+1`. `[Unit]`
    - Tip: Given `dueDate = 2026-10-19`, `today = 2026-10-20`, `isCompleted = false` / When `dueDateLabel` is called / Then it returns `"D+1"`.
  - D-Day boundary, due in one day: returns `D-1`. `[Unit]`
    - Tip: Given `dueDate = 2026-10-21`, `today = 2026-10-20`, `isCompleted = false` / When `dueDateLabel` is called / Then it returns `"D-1"`.
  - Completed/active boundary at D-Day: a completed task due today returns `CLEAR`, not `D-Day`. `[Unit]`
    - Tip: Given `dueDate = 2026-10-20`, `today = 2026-10-20`, `isCompleted = true` / When `dueDateLabel` is called / Then it returns `"CLEAR"`.
  - One-month boundary, future side, one day before it: returns `D-30`. `[Unit]`
    - Tip: Given `dueDate = 2026-11-19`, `today = 2026-10-20`, `isCompleted = false` / When `dueDateLabel` is called / Then it returns `"D-30"`.
  - One-month boundary, future side, exactly one calendar month later: returns `D-31` (31 days, not `D-Day`). `[Unit]`
    - Tip: Given `dueDate = 2026-11-20`, `today = 2026-10-20`, `isCompleted = false` / When `dueDateLabel` is called / Then it returns `"D-31"`.
  - One-month boundary, future side, one day after it: returns `D-32` (not `D-1`). `[Unit]`
    - Tip: Given `dueDate = 2026-11-21`, `today = 2026-10-20`, `isCompleted = false` / When `dueDateLabel` is called / Then it returns `"D-32"`.
  - One-month boundary, overdue side, one day before it: returns `D+29`. `[Unit]`
    - Tip: Given `dueDate = 2026-09-21`, `today = 2026-10-20`, `isCompleted = false` / When `dueDateLabel` is called / Then it returns `"D+29"`.
  - One-month boundary, overdue side, exactly one calendar month earlier: returns `D+30` (September has 30 days; not `D-Day`). `[Unit]`
    - Tip: Given `dueDate = 2026-09-20`, `today = 2026-10-20`, `isCompleted = false` / When `dueDateLabel` is called / Then it returns `"D+30"`.
  - One-month boundary, overdue side, one day after it: returns `D+31` (not `D+1`). `[Unit]`
    - Tip: Given `dueDate = 2026-09-19`, `today = 2026-10-20`, `isCompleted = false` / When `dueDateLabel` is called / Then it returns `"D+31"`.
  - Month end, short distance: the count continues across the month change. `[Unit]`
    - Tip: Given `dueDate = 2026-11-01`, `today = 2026-10-31`, `isCompleted = false` / When `dueDateLabel` is called / Then it returns `"D-1"`.
  - Year end, short distance: the count continues across the year change. `[Unit]`
    - Tip: Given `dueDate = 2027-01-01`, `today = 2026-12-31`, `isCompleted = false` / When `dueDateLabel` is called / Then it returns `"D-1"`.
  - One-year boundary, one day before it: returns `D-364`. `[Unit]`
    - Tip: Given `dueDate = 2027-10-19`, `today = 2026-10-20`, `isCompleted = false` / When `dueDateLabel` is called / Then it returns `"D-364"`.
  - One-year boundary, exactly one calendar year later: returns `D-365` (not `D-Day`). `[Unit]`
    - Tip: Given `dueDate = 2027-10-20`, `today = 2026-10-20`, `isCompleted = false` / When `dueDateLabel` is called / Then it returns `"D-365"`.
  - One-year boundary, one day after it: returns `D-366` (not `D-1`). `[Unit]`
    - Tip: Given `dueDate = 2027-10-21`, `today = 2026-10-20`, `isCompleted = false` / When `dueDateLabel` is called / Then it returns `"D-366"`.
  - Leap day inside a short range counts as one calendar day. `[Unit]`
    - Tip: Given `dueDate = 2028-03-01`, `today = 2028-02-28`, `isCompleted = false` / When `dueDateLabel` is called / Then it returns `"D-2"`.
  - One calendar month across a leap February is 29 days. `[Unit]`
    - Tip: Given `dueDate = 2028-03-01`, `today = 2028-02-01`, `isCompleted = false` / When `dueDateLabel` is called / Then it returns `"D-29"`.
  - One calendar month across a non-leap February is 28 days. `[Unit]`
    - Tip: Given `dueDate = 2027-03-01`, `today = 2027-02-01`, `isCompleted = false` / When `dueDateLabel` is called / Then it returns `"D-28"`.
  - Multi-month distance uses the total day count. `[Unit]`
    - Tip: Given `dueDate = 2027-01-28`, `today = 2026-10-20`, `isCompleted = false` / When `dueDateLabel` is called / Then it returns `"D-100"`.

**Undefined behavior** (optional)
- None. Time zones and the moment the local date changes are the caller's concern, since the function receives plain `LocalDate`s.
