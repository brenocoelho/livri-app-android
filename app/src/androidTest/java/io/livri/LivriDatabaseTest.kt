package io.livri

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.livri.database.LivriDatabase
import io.livri.database.user.UserDao
import io.livri.domain.User
import org.junit.After

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Before
import java.io.IOException

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class LivriDatabaseTest {

    private lateinit var userDao: UserDao
    private lateinit var db: LivriDatabase

    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        db = Room.inMemoryDatabaseBuilder(context, LivriDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        userDao = db.userDao
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(IOException::class)
    fun insertAndGetUser() {
        val user = User(
            "test-database-user-id",
            "FirstName",
            "Last Name",
            "username",
            "0123456789",
            "test@test.com",
            "987654321"
        )
        userDao.insert(user)
        var userCreated = userDao.get("test-database-user-id")
        assertEquals(userCreated?.firstName, "FirstName")
    }
}
