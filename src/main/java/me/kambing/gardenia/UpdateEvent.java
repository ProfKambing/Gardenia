package me.kambing.gardenia;

public class UpdateEvent {

    private double x, y, z;
    private float yaw, pitch;
    private boolean onGround;

    public UpdateEvent(double x, double y, double z, float yaw, float pitch, boolean onGround) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.onGround = onGround;
    }

    public static UpdateEvent convertPost(UpdateEvent e) {
        return new UpdateEvent(e.getX(), e.getY(), e.getZ(), e.getYaw(), e.getPitch(),
                e.isOnGround());
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public boolean isOnGround() {
        return onGround;
    }

    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }

    public static class LookEvent {

        private float pitch, prevPitch, yaw, prevYaw;

        public LookEvent(float pitch, float prevPitch, float yaw, float prevYaw) {
            this.pitch = pitch;
            this.prevPitch = prevPitch;
            this.yaw = yaw;
            this.prevYaw = prevYaw;
        }

        public LookEvent(float pitch, float yaw) {
            this.pitch = pitch;
            this.yaw = yaw;
        }

        public float getPitch() {
            return pitch;
        }

        public void setPitch(float pitch) {
            this.pitch = pitch;
        }

        public float getPrevPitch() {
            return prevPitch;
        }

        public void setPrevPitch(float prevPitch) {
            this.prevPitch = prevPitch;
        }

        public float getYaw() {
            return yaw;
        }

        public void setYaw(float yaw) {
            this.yaw = yaw;
        }

        public float getPrevYaw() {
            return prevYaw;
        }

        public void setPrevYaw(float prevYaw) {
            this.prevYaw = prevYaw;
        }


    }
    public static class EventRender3D {

        public float partialTicks;

        public EventRender3D(float partialTicks)
        {
            this.partialTicks = partialTicks;
        }

        public float getPartialTicks()
        {
            return partialTicks;
        }
    }
}
