package remix.myplayer

import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import remix.myplayer.ui.activity.ComposeActivity

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], application = App::class)
class StartupTest {

  @Test
  fun testAppCreation() {
    val app = RuntimeEnvironment.getApplication()
    println("App class is: ${app.javaClass.name}")
    assertNotNull(app)
  }

  @Test
  fun testActivityCreation() {
    val controller = Robolectric.buildActivity(ComposeActivity::class.java)
    controller.create()
    controller.start()
    controller.resume()
    assertNotNull(controller.get())
  }
}
