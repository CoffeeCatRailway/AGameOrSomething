package io.github.coffeecatrailway.agameorsomething.common.entity.ai;

import com.mojang.logging.LogUtils;
import io.github.coffeecatrailway.agameorsomething.common.entity.Entity;
import io.github.coffeecatrailway.agameorsomething.common.utils.MatUtils;
import io.github.coffeecatrailway.agameorsomething.common.utils.Timer;
import io.github.coffeecatrailway.agameorsomething.common.world.TileSet;
import io.github.coffeecatrailway.agameorsomething.common.world.World;
import io.github.coffeecatrailway.agameorsomething.core.AGameOrSomething;
import io.github.coffeecatrailway.agameorsomething.core.registry.TileRegistry;
import org.joml.*;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;

import java.lang.Math;
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
    private static final Set<Vector2ic> ADJACENT_POSITIONS = Set.of(new Vector2i(0, -1), new Vector2i(0, 1), new Vector2i(-1, 0), new Vector2i(1, 0));
//    private static final Set<Vector2ic> ADJACENT_POSITIONS = Set.of(new Vector2i(0, -1), new Vector2i(0, 1), new Vector2i(-1, 0), new Vector2i(1, 0), new Vector2i(-1, -1), new Vector2i(-1, 1), new Vector2i(1, -1), new Vector2i(1, 1));
    private static final int MAX_CHECKS = 2000;

    private final Vector2i destination = new Vector2i(0);
    public List<Vector2ic> path = new ArrayList<>();

    private final int wanderRadius;

    public PathFinderTask(Entity entity, int wanderRadius)
    {
        super(entity);
        this.wanderRadius = wanderRadius;
    }

    @Override
    public void tick(float delta, World world)
    {
        if (this.entity.getPosition().distance(this.destination.x, this.destination.y) < .5f || AGameOrSomething.getInstance().getKeyboardHandler().isKeyPressed(GLFW.GLFW_KEY_Q))
        {
            this.chooseDestination(world);
            this.aStar(this.path);
        }
    }

    private void chooseDestination(World world) // TODO: Check if position is reachable, walkable
    {
        int x = MatUtils.randomInt(world.random(), -this.wanderRadius, this.wanderRadius);
        int y = MatUtils.randomInt(world.random(), -this.wanderRadius, this.wanderRadius);
        this.entity.getPosition().get(RoundingMode.FLOOR, this.destination).add(x, y);
    }

    /**
     * Returns a path generated with the A* algorithm <br/>
     * Based on <a href="https://medium.com/@nicholas.w.swift/easy-a-star-pathfinding-7e6689c7f7b2">Easy A*</a>
     */
    private void aStar(List<Vector2ic> path)
    {
        if (this.entity.getWorld() == null)
        {
            LOGGER.warn("Entity {} does not have a world!", this.entity.getUUID());
            return;
        }

        List<Node> openNodes = new ArrayList<>();
        List<Node> closedNodes = new ArrayList<>();
        List<Node> children = new ArrayList<>();

        Node start = new Node(0, 0, new Vector2i(this.entity.getPosition(), RoundingMode.FLOOR));
        Node end = new Node(0, 0, this.destination);
        openNodes.add(start); // Add 'start' to 'openNodes'

        double timeStart = Timer.getTimeInSeconds();
        while (openNodes.size() > 0) // Loop through until 'end' is found
        {
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
            if (current.equals(end) || currentIndex > MAX_CHECKS)
            {
                path.clear();
                Node pathCurrent = current;
                while (pathCurrent != null)
                {
                    path.add(pathCurrent.position);
                    pathCurrent = pathCurrent.parent;
                }
                Collections.reverse(path);
                double timeEnd = Timer.getTimeInSeconds();
                LOGGER.debug("Entity {} took {} seconds to find path to {}", this.entity.getUUID(), (timeEnd - timeStart), end.position);
                return;
            }

            // Generate 'children'
            children.clear();
            for (Vector2ic newPosition : ADJACENT_POSITIONS) // TODO: Check if entity fits, diagonals
            {
                boolean skip = false;
                // Get node position
                Vector2i nodePosition = new Vector2i(current.position).add(newPosition);

                // Make sure 'nodePosition' is inside the world
                if (nodePosition.x < -this.entity.getWorld().getWorldRadius() || nodePosition.x > this.entity.getWorld().getWorldRadius() ||
                        nodePosition.y < -this.entity.getWorld().getWorldRadius() || nodePosition.y > this.entity.getWorld().getWorldRadius())
                    skip = true;

                // Make sure position is walkable
//                if (!this.entity.getWorld().isPathfindable(nodePosition))
                if (!this.entity.getWorld().getTile(nodePosition, TileSet.Level.FOREGROUND).equals(TileRegistry.AIR.get()))
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
        private final Vector2ic position;

        private Node(Node parent, Vector2ic position)
        {
            this.g = parent.g;
            this.h = parent.h;
            this.parent = parent;
            this.position = position;
        }

        private Node(float g, float h, Vector2ic position)
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
