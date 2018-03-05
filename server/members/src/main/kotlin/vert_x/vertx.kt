package vert_x
import vertxl.VertX

object vertx {
    fun start() {
        VertX.start()
    }
    fun deployVerticals() {
        val v = VertX.vertx!!

        v.deployVerticle(vert_x.members.loginreg.login())
        v.deployVerticle(vert_x.members.loginreg.register())
        v.deployVerticle(vert_x.members.loginreg.register_anonymous())
        v.deployVerticle(vert_x.members.loginreg.confirm_email())
        v.deployVerticle(vert_x.members.loginreg.recover_password())
        v.deployVerticle(vert_x.members.loginreg.two_factor_activate())
        v.deployVerticle(vert_x.members.loginreg.two_factor_deactivate())
        v.deployVerticle(vert_x.members.loginreg.two_factor_key_get())
        v.deployVerticle(vert_x.members.loginreg.two_factor_key_set())
        v.deployVerticle(vert_x.members.loginreg.verify_password())
        v.deployVerticle(vert_x.members.loginreg.update_password())
        v.deployVerticle(vert_x.members.loginreg.anonymous_upgrade_to_email())

        v.deployVerticle(vert_x.members.member_v1.get())
        v.deployVerticle(vert_x.members.member_v1.get_my_privates()) // used on anonymous accounts

    }
}