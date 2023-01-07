package io.github.coffeecatrailway.agameorsomething.common.entity.ai;

import com.mojang.logging.LogUtils;
import io.github.coffeecatrailway.agameorsomething.common.entity.Entity;
import io.github.coffeecatrailway.agameorsomething.common.utils.MatUtils;
import io.github.coffeecatrailway.agameorsomething.common.utils.Timer;
import io.github.coffeecatrailway.agameorsomething.common.world.TileSet;
import io.github.coffeecatrailway.agameorsomething.common.world.World;
import io.github.coffeecatrailway.agameorsomething.core.AGameOrSomething;
import io.github.coffeecatrailway.agameorsomething.core.registry.TileRegistry;
import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.joml.Vector2i;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * @author CoffeeCatRailway
 * Created: 07/01/2023
 */
public class PathFinderTask extends Task
{
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Set<Vector2fc> ADJACENT_POSITIONS = Set.of(new Vector2f(0f, -1f), new Vector2f(0f, 1f), new Vector2f(-1f, 0f), new Vector2f(1f, 0f), new Vector2f(-1f, -1f), new Vector2f(-1f, 1f), new Vector2f(1f, -1f), new Vector2f(1f, 1f));

    private final Vector2f nextPos = new Vector2f(0f);
    public List<Vector2fc> path;

    public PathFinderTask(Entity entity)
    {
        super(entity);
        this.path = this.aStar();
    }

    @Override
    public void tick(float delta, World world)
    {
        if (this.entity.getPosition().distance(this.nextPos) < .5f || AGameOrSomething.getInstance().getKeyboardHandler().isKeyPressed(GLFW.GLFW_KEY_Q))
        {
//            this.pickNextPos(world);
            this.path = this.aStar();
        }
    }

    private void pickNextPos(World world) // TODO: Check if position is reachable and/or path-findable
    {
        float x = MatUtils.randomFloat(world.random(), -world.getWorldRadius() + 2, world.getWorldRadius() - 2);
        float y = MatUtils.randomFloat(world.random(), -world.getWorldRadius() + 2, world.getWorldRadius() - 2);
        this.nextPos.set(x, y);
    }

    /**
     * Returns a path generated with the A* algorithm <br/>
     * Based on <a href="https://medium.com/@nicholas.w.swift/easy-a-star-pathfinding-7e6689c7f7b2">Easy A*</a>
     */
    private List<Vector2fc> aStar()
    {
        if (this.entity.getWorld() == null)
            return new ArrayList<>();

        List<Node> openNodes = new ArrayList<>();
        List<Node> closedNodes = new ArrayList<>();

        Node start = new Node(0, 0, this.entity.getPosition());
        Node end = new Node(0, 0, this.nextPos);
        openNodes.add(start); // Add 'start' to 'openNodes'

        int timeLast = (int) Timer.getTimeInSeconds();
        while (openNodes.size() > 0) // Loop through until 'end' is found
        {
            int time = (int) Timer.getTimeInSeconds();
            int timePassed = Math.abs(time - timeLast);
            timeLast = time;

            // Find node with lowest f value
            Node current = openNodes.get(0);
            int currentIndex = 0;
            for (int i = 0; i < openNodes.size(); i++)
            {
                if (openNodes.get(i).f() < current.f())
                {
                    current = openNodes.get(i);
                    currentIndex = i;
                }
            }

            // Move 'current' from 'openNodes' to 'closedNodes'
            openNodes.remove(currentIndex);
            closedNodes.add(current);

            // Found end node
            if (current.equals(end))
            {
                List<Vector2fc> path = new ArrayList<>();
                Node pathCurrent = current;
                while (pathCurrent != null)
                {
                    path.add(pathCurrent.position);
                    pathCurrent = pathCurrent.parent;
                }
                Collections.reverse(path);
                LOGGER.debug("Entity {} took {} seconds to find path to {}", this.entity.getUUID(), timePassed, end.position);
                return path;
            }

            // Generate 'children'
            List<Node> children = new ArrayList<>();
            for (Vector2fc newPosition : ADJACENT_POSITIONS)
            {
                boolean skip = false;
                // Get node position
                Vector2f nodePosition = current.position.add(newPosition, new Vector2f());

                // Make sure 'nodePosition' is inside the world
                if (nodePosition.x < -this.entity.getWorld().getWorldRadius() || nodePosition.x > this.entity.getWorld().getWorldRadius() ||
                        nodePosition.y < -this.entity.getWorld().getWorldRadius() || nodePosition.y > this.entity.getWorld().getWorldRadius())
                    skip = true;

                // Make sure position is walkable
//                if (!this.entity.getWorld().isPathfindable(nodePosition))
                if (!this.entity.getWorld().getTile(new Vector2i((int) nodePosition.x, (int) nodePosition.y), TileSet.Level.FOREGROUND).equals(TileRegistry.AIR.get()))
                    skip = true;

                if (skip)
                    continue;
                children.add(new Node(current, nodePosition));
            }

            // Loop through 'children'
            for (Node child : children)
            {
                boolean skip = false;

                // Check if child is in 'closedNodes'
                for (Node closed : closedNodes)
                {
                    if (child.equals(closed))
                    {
                        skip = true;
                        break;
                    }
                }
                if (skip)
                    continue;

                // Create f, g & h values
                child.g = current.g + 1f;
                child.h = (float) (Math.pow((child.position.x() - end.position.x()), 2f) + Math.pow((child.position.y() - end.position.y()), 2f));

                // Check if child is in 'openNodes'
                for (Node open : openNodes)
                {
                    if (child.equals(open) && child.g > open.g)
                    {
                        skip = true;
                        break;
                    }
                }
                if (skip)
                    continue;

                // Add child to 'openNodes'
                openNodes.add(child);
            }
        }

        LOGGER.warn("Entity {} failed to find path to {}", this.entity.getUUID(), end.position);
        return new ArrayList<>();
    }

    /**
     * f is the total cost of the node (g + h) <br/>
     * g is the distance between the current node and the start node <br/>
     * h is the heuristic - estimated distance from the current node to the end node
     */
    private static final class Node
    {
        private float g;
        private float h;
        private final Node parent;
        private final Vector2fc position;

        private Node(Node parent, Vector2fc position)
        {
            this.g = parent.g;
            this.h = parent.h;
            this.parent = parent;
            this.position = position;
        }

        private Node(float g, float h, Vector2fc position)
        {
            this.g = g;
            this.h = h;
            this.parent = null;
            this.position = position;
        }

        public float f()
        {
            return this.g + this.h;
        }

        @Override
        public boolean equals(Object obj)
        {
            return obj instanceof Node && ((Node) obj).position.x() == this.position.x() && ((Node) obj).position.y() == this.position.y();
        }
    }
}
