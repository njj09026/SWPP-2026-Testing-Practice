package com.example.swpp.tododapp.tasks

import android.view.View
import android.widget.TextView
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.swpp.tododapp.R
import com.example.swpp.tododapp.ServiceLocator
import com.example.swpp.tododapp.data.Task
import com.example.swpp.tododapp.data.source.FakeAndroidTestRepository
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlinx.coroutines.runBlocking
import java.time.LocalDate

@RunWith(AndroidJUnit4::class)
class TasksListBindingsTest {
    private lateinit var repository: FakeAndroidTestRepository

    @Before fun setUp() {
        repository = FakeAndroidTestRepository()
        ServiceLocator.tasksRepository = repository
    }

    @After fun tearDown() {
        ServiceLocator.resetRepository()
    }

    private fun task(completed: Boolean = false, dueDate: LocalDate? = null) =
        Task("T", "D", isCompleted = completed, id = "id1", dueDate = dueDate)

    private fun newTextView() = TextView(InstrumentationRegistry.getInstrumentation().targetContext)

    private fun bind(view: TextView, task: Task?) {
        // When the row is bound on the main thread
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            setDueDateLabel(view, task)
        }
    }

    private fun assertVisibleLabel(view: TextView, expected: String) {
        // Then both the text and visibility show the exact label
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            assertEquals(expected, view.text.toString())
            assertEquals(View.VISIBLE, view.visibility)
        }
    }

    private fun assertNoLabel(view: TextView) {
        // Then no label text can be seen
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            assertTrue(view.text.toString().isEmpty() || view.visibility != View.VISIBLE)
        }
    }

    private fun assertScreenNoLabel() {
        onView(withId(R.id.due_date_text)).check { view, error ->
            if (error != null) throw error
            val label = view as TextView
            assertTrue(label.text.toString().isEmpty() || label.visibility != View.VISIBLE)
        }
    }

    @Test fun setDueDateLabel_completedWithoutDate_showsClear() {
        // Given a new row and a completed task without a due date
        val view = newTextView()
        bind(view, task(completed = true))
        assertVisibleLabel(view, "CLEAR")
    }

    @Test fun setDueDateLabel_completedFuture_showsClear() {
        // Given a completed task due in five days
        val view = newTextView()
        bind(view, task(completed = true, dueDate = LocalDate.now().plusDays(5)))
        assertVisibleLabel(view, "CLEAR")
    }

    @Test fun setDueDateLabel_fiveDaysFuture_showsDMinus5() {
        // Given an active task due in five days
        val view = newTextView()
        bind(view, task(dueDate = LocalDate.now().plusDays(5)))
        assertVisibleLabel(view, "D-5")
    }

    @Test fun setDueDateLabel_fiveDaysOverdue_showsDPlus5() {
        // Given an active task overdue by five days
        val view = newTextView()
        bind(view, task(dueDate = LocalDate.now().minusDays(5)))
        assertVisibleLabel(view, "D+5")
    }

    @Test fun setDueDateLabel_dueToday_showsDDay() {
        // Given an active task due today
        val view = newTextView()
        bind(view, task(dueDate = LocalDate.now()))
        assertVisibleLabel(view, "D-Day")
    }

    @Test fun setDueDateLabel_activeWithoutDate_showsNoLabel() {
        // Given an active task without a due date
        val view = newTextView()
        bind(view, task())
        assertNoLabel(view)
    }

    @Test fun setDueDateLabel_nullTask_showsNoLabel() {
        // Given a row without a task
        val view = newTextView()
        bind(view, null)
        assertNoLabel(view)
    }

    @Test fun setDueDateLabel_oneDayOverdue_showsDPlus1() {
        // Given an active task overdue by one day
        val view = newTextView()
        bind(view, task(dueDate = LocalDate.now().minusDays(1)))
        assertVisibleLabel(view, "D+1")
    }

    @Test fun setDueDateLabel_oneDayFuture_showsDMinus1() {
        // Given an active task due in one day
        val view = newTextView()
        bind(view, task(dueDate = LocalDate.now().plusDays(1)))
        assertVisibleLabel(view, "D-1")
    }

    @Test fun setDueDateLabel_thirtyOneDaysFuture_showsDMinus31() {
        // Given an active task due in 31 days
        val view = newTextView()
        bind(view, task(dueDate = LocalDate.now().plusDays(31)))
        assertVisibleLabel(view, "D-31")
    }

    @Test fun setDueDateLabel_thirtyOneDaysOverdue_showsDPlus31() {
        // Given an active task overdue by 31 days
        val view = newTextView()
        bind(view, task(dueDate = LocalDate.now().minusDays(31)))
        assertVisibleLabel(view, "D+31")
    }

    @Test fun setDueDateLabel_uncheckedWithoutDate_removesClear() {
        // Given the same row already showing CLEAR for a completed task
        val view = newTextView()
        val completed = task(completed = true)
        bind(view, completed)
        assertVisibleLabel(view, "CLEAR")
        // When it is rebound after unchecking
        bind(view, completed.copy(isCompleted = false))
        assertNoLabel(view)
    }

    @Test fun setDueDateLabel_uncheckedWithDate_showsDMinus3() {
        // Given the same row already showing CLEAR for a completed task
        val view = newTextView()
        val completed = task(completed = true, dueDate = LocalDate.now().plusDays(3))
        bind(view, completed)
        assertVisibleLabel(view, "CLEAR")
        // When it is rebound after unchecking
        bind(view, completed.copy(isCompleted = false))
        assertVisibleLabel(view, "D-3")
    }

    @Test fun setDueDateLabel_checkedWithDate_showsClear() {
        // Given the same row already showing D-3 for an active task
        val view = newTextView()
        val active = task(dueDate = LocalDate.now().plusDays(3))
        bind(view, active)
        assertVisibleLabel(view, "D-3")
        // When it is rebound after checking
        bind(view, active.copy(isCompleted = true))
        assertVisibleLabel(view, "CLEAR")
    }

    @Test fun setDueDateLabel_recycledWithoutDate_removesPreviousLabel() {
        // Given a row already showing D-5 for another task
        val view = newTextView()
        bind(view, task(dueDate = LocalDate.now().plusDays(5)))
        assertVisibleLabel(view, "D-5")
        // When it is rebound to an active task without a date
        bind(view, task().copy(id = "id2"))
        assertNoLabel(view)
    }

    @Test fun setDueDateLabel_reboundToNull_removesPreviousLabel() {
        // Given a row already showing CLEAR
        val view = newTextView()
        bind(view, task(completed = true))
        assertVisibleLabel(view, "CLEAR")
        // When it is rebound before its next task arrives
        bind(view, null)
        assertNoLabel(view)
    }

    @Test fun tasksList_checkThenUncheckWithoutDate_showsClearThenNoLabel() {
        // Given the only task is active and has no due date
        runBlocking { repository.saveTask(Task("TITLE1", "DESCRIPTION1", id = "id1")) }
        launchFragmentInContainer<TasksFragment>(themeResId = R.style.AppTheme)
        // When checked, then the row shows CLEAR
        onView(withId(R.id.complete_checkbox)).perform(click())
        onView(withId(R.id.due_date_text)).check(matches(withText("CLEAR")))
        onView(withId(R.id.due_date_text)).check(matches(isDisplayed()))
        // When unchecked, then the row shows no label
        onView(withId(R.id.complete_checkbox)).perform(click())
        assertScreenNoLabel()
    }

    @Test fun tasksList_checkThenUncheckWithDate_showsDMinus3ThenClearThenDMinus3() {
        // Given the only task is active and due in three days
        runBlocking { repository.saveTask(Task("TITLE1", "DESCRIPTION1", id = "id1", dueDate = LocalDate.now().plusDays(3))) }
        launchFragmentInContainer<TasksFragment>(themeResId = R.style.AppTheme)
        onView(withId(R.id.due_date_text)).check(matches(withText("D-3")))
        onView(withId(R.id.due_date_text)).check(matches(isDisplayed()))
        // When checked, then the row shows CLEAR
        onView(withId(R.id.complete_checkbox)).perform(click())
        onView(withId(R.id.due_date_text)).check(matches(withText("CLEAR")))
        onView(withId(R.id.due_date_text)).check(matches(isDisplayed()))
        // When unchecked, then the row shows D-3 again
        onView(withId(R.id.complete_checkbox)).perform(click())
        onView(withId(R.id.due_date_text)).check(matches(withText("D-3")))
        onView(withId(R.id.due_date_text)).check(matches(isDisplayed()))
    }
}
